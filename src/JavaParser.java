import generator.JavaGenerator;
import generator.Token;
import scope.*;

import java.util.ArrayList;

public class JavaParser {
    private ArrayList<Token> tokens;
    private int tokenIndex = 0;
    private Exception ex;
    private Scope currentScope;
    private JavaGenerator generator;

    public JavaParser(Scope globalScope, JavaGenerator generator) {
        this.currentScope = globalScope;
        this.generator = generator;
    }

    public void analyze(JavaLexer lexer) throws Exception {
        tokens = lexer.getTokens();
        currentScope.define(new BuiltInTypeSymbol("int"));
        currentScope.define(new BuiltInTypeSymbol("float"));
        currentScope.define(new BuiltInTypeSymbol("double"));
        currentScope.define(new BuiltInTypeSymbol("void"));
        currentScope.define(new BuiltInTypeSymbol("Scanner"));

        if (Program()) {
            if (tokenIndex == tokens.size()) {
                return;
            }
        }

        throw ex;
    }

    private boolean match(String name) {
        if (tokens.get(tokenIndex).getType().getName().equals(name)) {
            tokenIndex++;
            return true;
        }

        ex = new SyntaxException("Se esperaba: '" + name + "' y se encontro: '" + tokens.get(tokenIndex).getType().getName() + "'");
        return false;
    }

    private void define() {
        BuiltInTypeSymbol b = (BuiltInTypeSymbol) currentScope.resolve(tokens.get(tokenIndex - 2).getName());
        currentScope.define(new VariableSymbol(tokens.get(tokenIndex - 1).getName(), b));
    }

    private void defineMethod(ArrayList<VariableSymbol> parameters, String methodType, String methodIdentifier) {
        BuiltInTypeSymbol b = (BuiltInTypeSymbol) currentScope.resolve(methodType);
        MethodSymbol m = new MethodSymbol(methodIdentifier, parameters, currentScope);
        currentScope.define(m);
        currentScope = m;
    }

    private void resolveTypeParams(ArrayList<VariableSymbol> parameters) {
        BuiltInTypeSymbol b = (BuiltInTypeSymbol) currentScope.resolve(tokens.get(tokenIndex - 2).getName());
        parameters.add(new VariableSymbol(tokens.get(tokenIndex - 1).getName(), b));
    }

    private boolean resolve() {
        VariableSymbol v = (VariableSymbol) currentScope.resolve(tokens.get(tokenIndex - 1).getName());
        return resolveInternal(v, tokenIndex - 1);
    }

    private boolean resolveMethod() {
        MethodSymbol m = (MethodSymbol) currentScope.resolve(tokens.get(tokenIndex - 2).getName());
        return resolveInternal(m, tokenIndex - 2);
    }

    private boolean resolveInternal(Symbol s, int identifierIndex) {
        if (s == null) {
            ex = new SemanticException("La variable: '" + tokens.get(identifierIndex).getName() + "' no fue declarada.");
            return false;
        }
        return true;
    }

    private boolean Program() {
        if (match(JavaLexer.PUBLIC)) {
            if (match(JavaLexer.CLASS)) {
                if (match(JavaLexer.IDENTIFIER)) {
                    if (match(JavaLexer.LEFT_BRACKET)) {
                        Declarations();
                        Methods();
                        if (match(JavaLexer.PUBLIC)) {
                            if (match(JavaLexer.STATIC)) {
                                if (match(JavaLexer.VOID)) {
                                    if (match(JavaLexer.MAIN)) {
                                        if (match(JavaLexer.LEFT_PARENTHESIS)) {
                                            if (match(JavaLexer.STR_ARR)) {
                                                if (match(JavaLexer.IDENTIFIER)) {
                                                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                                        if (match(JavaLexer.LEFT_BRACKET)) {
                                                            if (Sentences()) {
                                                                if (!(ex instanceof SemanticException)) {
                                                                    if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                        if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                            generator.createTupleProgramEnd();
                                                                            return true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return false;
    }

    private boolean Methods() {
        int auxIndex = tokenIndex;
        if (Method()) {
            while (Method()) ;
            return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Declarations() {
        int auxIndex = tokenIndex;
        if (Declaration()) {
            while (Declaration());
            return true;
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Method() {
        ArrayList<VariableSymbol> params = new ArrayList<>();
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();

        if (match(JavaLexer.PUBLIC)) {
            if (match(JavaLexer.STATIC)) {
                if (match(JavaLexer.VOID)) {
                    if (match(JavaLexer.IDENTIFIER)) {
                        if (match(JavaLexer.LEFT_PARENTHESIS)) {
                            if (Parameter(params)) {
                                while (match(JavaLexer.COMMA)) {
                                    if (!Parameter(params)) return false;
                                }
                            }
                            if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                defineMethod(params, tokens.get(auxIndex + 2).getName(), tokens.get(auxIndex + 3).getName());
                                if (match(JavaLexer.LEFT_BRACKET)) {
                                    generator.createMethodTuple();
                                    if (Sentences()) {
                                        if (match(JavaLexer.RIGHT_BRACKET)) {
                                            currentScope = currentScope.getEnclosingScope();
                                            generator.createEndMethodTuple();
                                            generator.connectMethod(tupleIndex, (MethodSymbol) currentScope.resolve(tokens.get(auxIndex + 3).getName()));
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Parameter(ArrayList<VariableSymbol> parameters) {
        int auxIndex = tokenIndex;
        if (BasicTypes()) {
            if (match(JavaLexer.IDENTIFIER)) {
                resolveTypeParams(parameters);
                return true;
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean InvokeMethod() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();
        ArrayList<Token> arguments = new ArrayList<>();
        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (resolveMethod()) {
                    if (Value()) {
                        arguments.add(tokens.get(tokenIndex-1));
                        while (match(JavaLexer.COMMA)) {
                            if (!Value()) return false;
                            arguments.add(tokens.get(tokenIndex-1));
                        }
                    }
                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createInvokeMethodTuple(tupleIndex, (MethodSymbol) currentScope.resolve(tokens.get(auxIndex).getName()), arguments, generator.getTuples());
                            return true;
                        }
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Declaration() {
        int auxIndex = tokenIndex;

        if (BasicTypes()) {
            if (match(JavaLexer.IDENTIFIER)) {
                define();
                if (match(JavaLexer.SEMICOLON)) {
                    return true;
                }

                if (match(JavaLexer.EQUALS)) {
                    if (match(JavaLexer.NUM)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleAssignment(auxIndex + 1, tokenIndex);
                            return true;
                        }
                    }

                    if (Read()) return true;
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.IDENTIFIER)) {
                define();
                if (match(JavaLexer.EQUALS)) {
                    if (match(JavaLexer.NEW)) {
                        if (match(JavaLexer.IDENTIFIER)) {
                            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                                if (Value()) {
                                    while (match(JavaLexer.COMMA)) {
                                        if (!Value()) return false;
                                    }
                                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                        if (match(JavaLexer.SEMICOLON)) return true;
                                    }
                                }

                                if (match(JavaLexer.IN)) {
                                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                        if (match(JavaLexer.SEMICOLON)) return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Sentences() {
        int auxIndex = tokenIndex;
        if (Sentence()) {
            while (Sentence()) ;
            return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Sentence() {
        int auxIndex = tokenIndex;

        if (Declaration()) return true;

        tokenIndex = auxIndex;

        if (InvokeMethod()) return true;

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.IDENTIFIER)
                && !(ex instanceof SemanticException)) {
            if (Assignment()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.WRITE)) {
            if (Write()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.IF)) {
            if (If()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.WHILE)) {
            if (While()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.FOR)) {
            if (For()) return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Assignment() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.EQUALS)) {
                    if (Read()) {
                        return true;
                    }
                }
            }
        }

        tokenIndex = auxIndex;

        if (IncrementDecrement()) {
            if (match(JavaLexer.SEMICOLON)) {
                generator.createTupleAssignment(auxIndex, tokenIndex);
                return true;
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.EQUALS)) {
                    if (Expression()) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleAssignment(auxIndex, tokenIndex);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.ARITHMETIC) || match(JavaLexer.PLUS) || match(JavaLexer.MINUS)) {
                    if (match(JavaLexer.EQUALS)) {
                        if (Value()) {
                            if (match(JavaLexer.SEMICOLON)) {
                                generator.createTupleAssignment(auxIndex, tokenIndex + 2);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean IncrementDecrement() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.PLUS) && match(JavaLexer.PLUS) ||
                        match(JavaLexer.MINUS) && match(JavaLexer.MINUS)) {
                        return true;
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Expression() {
        int auxIndex = tokenIndex;

        if (Value()) {
            if (match(JavaLexer.ARITHMETIC) || match(JavaLexer.PLUS) || match(JavaLexer.MINUS)) {
                if (Value()) {
                    return true;
                }
            }
        }

        tokenIndex = auxIndex;

        if (Value()) {
            return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Value() {
        if (match(JavaLexer.IDENTIFIER)) return resolve();
        return match(JavaLexer.NUM);
    }

    private boolean Read() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.DOT)) {
                    if (match(JavaLexer.READ)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleRead(auxIndex - 2);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Write() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.WRITE)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.STRING)) {
                    if (match(JavaLexer.PLUS)) {
                        if (match(JavaLexer.IDENTIFIER)) {
                            if (resolve()) {
                                if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                    if (match(JavaLexer.SEMICOLON)) {
                                        generator.createTupleWrite(auxIndex, tokenIndex);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.WRITE)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.STRING)) {
                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleWrite(auxIndex, tokenIndex);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.WRITE)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.IDENTIFIER)) {
                    if (resolve()) {
                        if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                            if (match(JavaLexer.SEMICOLON)) {
                                generator.createTupleWrite(auxIndex, tokenIndex);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean If() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();

        if (match(JavaLexer.IF)) {
            if (Comparison()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Sentences()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
                            generator.connectIf(tupleIndex);
                            if (ElseIf()) {
                                while (ElseIf());
                            }
                            if (Else()) return true;
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean ElseIf() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();
        if (match(JavaLexer.ELSE)) {
            if (match(JavaLexer.IF)) {
                if (Comparison()) {
                    if (match(JavaLexer.LEFT_BRACKET)) {
                        if (Sentences()) {
                            if (match(JavaLexer.RIGHT_BRACKET)) {
                                generator.connectIf(tupleIndex);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Else() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.ELSE)) {
            if (match(JavaLexer.LEFT_BRACKET)) {
                if (Sentences()) {
                    if (match(JavaLexer.RIGHT_BRACKET)) {
                        return true;
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Comparison() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.LEFT_PARENTHESIS)) {
            if (Value()) {
                if (match(JavaLexer.RELATIONAL)) {
                    if (Value()) {
                        if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                            generator.createTupleComparison(auxIndex + 1);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean While() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();

        if (match(JavaLexer.WHILE)) {
            if (Comparison()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Sentences()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
                            generator.connectWhile(tupleIndex);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean For() {
        int auxIndex = tokenIndex;
        int incrementDecrementIndex;

        if (match(JavaLexer.FOR)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.INT)) {
                    if (match(JavaLexer.IDENTIFIER)) {
                        define();
                        if (match(JavaLexer.EQUALS)) {
                            if (match(JavaLexer.NUM)) {
                                if (match(JavaLexer.SEMICOLON)) {
                                    generator.createTupleAssignment(auxIndex+3, tokenIndex);
                                    if (Value()) {
                                        if (match(JavaLexer.RELATIONAL)) {
                                            if (Value()) {
                                                if (match(JavaLexer.SEMICOLON)) {
                                                    int tupleIndex = generator.getTuples().size();
                                                    generator.createTupleComparison(auxIndex + 7);
                                                    incrementDecrementIndex = tokenIndex;
                                                    if (IncrementDecrement()) {
                                                        if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                                            if (match(JavaLexer.LEFT_BRACKET)) {
                                                                if (Sentences()) {
                                                                    if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                        generator.createTupleAssignment(incrementDecrementIndex, incrementDecrementIndex + 8);
                                                                        generator.connectWhile(tupleIndex);
                                                                        return true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean BasicTypes() {
        return match(JavaLexer.INT) || match(JavaLexer.FLOAT) || match(JavaLexer.DOUBLE);
    }
}
