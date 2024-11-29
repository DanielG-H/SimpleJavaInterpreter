import generator.JavaGenerator;
import generator.Token;
import scope.*;

import java.util.ArrayList;

public class JavaParser {
    private ArrayList<Token> tokens;
    private int tokenIndex = 0;
    private SyntaxException ex;
    private Scope currentScope;
    private JavaGenerator generator;

    public JavaParser(Scope globalScope, JavaGenerator generator) {
        this.currentScope = globalScope;
        this.generator = generator;
    }

    public void analyze(JavaLexer lexer) throws SyntaxException {
        tokens = lexer.getTokens();
        currentScope.define(new BuiltInTypeSymbol("int"));
        currentScope.define(new BuiltInTypeSymbol("float"));
        currentScope.define(new BuiltInTypeSymbol("double"));
        currentScope.define(new BuiltInTypeSymbol("char"));
        currentScope.define(new BuiltInTypeSymbol("String"));
        currentScope.define(new BuiltInTypeSymbol("boolean"));
        currentScope.define(new BuiltInTypeSymbol("void"));
        currentScope.define(new BuiltInTypeSymbol("Scanner"));

        if (Program()) {
            if (tokenIndex == tokens.size()) {
                System.out.println("\nLa sintaxis del programa es correcta.");
                return;
            }
        }

        throw ex;
    }

    private boolean match(String name) {
        if (tokens.get(tokenIndex).getType().getName().equals(name)) {
            System.out.println(name + ": " + tokens.get(tokenIndex).getName());
            tokenIndex++;
            return true;
        }

        ex = new SyntaxException("Se esperaba: '" + name + "' y se encontro: '" + tokens.get(tokenIndex).getType().getName() + "'");
        return false;
    }

    private void define() throws SemanticException {
        BuiltInTypeSymbol b = (BuiltInTypeSymbol) currentScope.resolve(tokens.get(tokenIndex - 2).getName());
        if (b == null) {
            throw new SemanticException("El tipo: '" + tokens.get(tokenIndex - 2).getName() + "' no fue definido.");
        }
        currentScope.define(new VariableSymbol(tokens.get(tokenIndex - 1).getName(), b));
        System.out.println("Definida");
    }

    private boolean resolve() throws SemanticException {
        VariableSymbol v = (VariableSymbol) currentScope.resolve(tokens.get(tokenIndex - 1).getName());
        return resolveInternal(v);
    }

    private boolean resolveMethod() {
        MethodSymbol m = (MethodSymbol) currentScope.resolve(tokens.get(tokenIndex - 1).getName());
        return resolveInternal(m);
    }

    private boolean resolveInternal(Symbol s) {
        if (s == null) {
            ex = new SyntaxException("La variable: '" + tokens.get(tokenIndex - 1).getName() + "' no fue declarada.");
            return false;
        }
        System.out.println("Resuelta");
        return true;
    }

    private boolean Program() {
        if (match(JavaLexer.PUBLIC)) {
            if (match(JavaLexer.CLASS)) {
                if (match(JavaLexer.IDENTIFIER)) {
                    if (match(JavaLexer.LEFT_BRACKET)) {
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
                                                            if (Enunciados()) {
                                                                if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                    if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                        generator.createTupleFinPrograma();
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

    private boolean Method() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.PUBLIC)) {
            if (match(JavaLexer.STATIC)) {
                if (basicTypes() || match(JavaLexer.VOID)) {
                    if (match(JavaLexer.IDENTIFIER)) {
                        if (match(JavaLexer.LEFT_PARENTHESIS)) {
                            if (parameter()) {
                                while (match(JavaLexer.COMMA)) {
                                    if (!parameter()) return false;
                                }
                            }
                            if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                if (match(JavaLexer.LEFT_BRACKET)) {
                                    if (Enunciados()) {
                                        if (match(JavaLexer.RIGHT_BRACKET)) {

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

    private boolean parameter() {
        int auxIndex = tokenIndex;
        if (basicTypes()) {
            if (match(JavaLexer.IDENTIFIER)) return true;
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean invokeMethod() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (Valor()) {
                    while (match(JavaLexer.COMMA)) {
                        if (!Valor()) return false;
                    }
                }
                if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                    if (match(JavaLexer.SEMICOLON)) return true;
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Declaracion() {
        int auxIndex = tokenIndex;

        if (basicTypes()) {
            if (match(JavaLexer.IDENTIFIER)) {
                define();
                if (match(JavaLexer.SEMICOLON)) {
                    return true;
                }

                if (match(JavaLexer.EQUALS)) {
                    if (match(JavaLexer.STRING) || match(JavaLexer.NUM) || match(JavaLexer.BOOL)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleAsignacion(auxIndex+1, tokenIndex);
                            return true;
                        }
                    }

                    if (Leer()) return true;
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
                                if (Valor()) {
                                    while (match(JavaLexer.COMMA)) {
                                        if (!Valor()) return false;
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

    private boolean Enunciados() {
        int auxIndex = tokenIndex;
        if (Enunciado()) {
            while (Enunciado()) ;
            return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Enunciado() {
        int auxIndex = tokenIndex;

        if (Declaracion()) return true;

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.IDENTIFIER)) {
            if (Asignacion()) return true;
        }

        tokenIndex = auxIndex;

        if (invokeMethod()) return true;

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.WRITE)) {
            if (Escribir()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.IF)) {
            if (Si()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.WHILE)) {
            if (Mientras()) return true;
        }

        tokenIndex = auxIndex;

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.FOR)) {
            if (Repite()) return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Asignacion() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.EQUALS)) {
                    if (Leer()) {
                        return true;
                    }
                }
            }
        }

        tokenIndex = auxIndex;

        if (IncrementDecrement()) {
            if (match(JavaLexer.SEMICOLON)) {
                generator.createTupleAsignacion(auxIndex, tokenIndex);
                return true;
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.EQUALS)) {
                    if (Expresion()) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleAsignacion(auxIndex, tokenIndex);
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
                        if (Valor()) {
                            if (match(JavaLexer.SEMICOLON)) {
                                generator.createTupleAsignacion(auxIndex, tokenIndex+2);
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

    private boolean Expresion() {
        int auxIndex = tokenIndex;

        if (Valor()) {
            if (match(JavaLexer.ARITHMETIC) || match(JavaLexer.PLUS) || match(JavaLexer.MINUS)) {
                if (Valor()) {
                    return true;
                }
            }
        }

        tokenIndex = auxIndex;

        if (Valor()) {
            return true;
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Valor() {
        if (match(JavaLexer.IDENTIFIER)) return resolve();
        return match(JavaLexer.NUM);
    }

    private boolean Leer() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.IDENTIFIER)) {
            if (resolve()) {
                if (match(JavaLexer.DOT)) {
                    if (match(JavaLexer.READ)) {
                        if (match(JavaLexer.SEMICOLON)) {
                            generator.createTupleLeer(auxIndex-2);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Escribir() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.WRITE)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.STRING)) {
                    if (match(JavaLexer.PLUS)) {
                        if (match(JavaLexer.IDENTIFIER)) {
                            if (resolve()) {
                                if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                    if (match(JavaLexer.SEMICOLON)) {
                                        generator.createTupleEscribir(auxIndex, tokenIndex);
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
                            generator.createTupleEscribir(auxIndex, tokenIndex);
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
                                generator.createTupleEscribir(auxIndex, tokenIndex);
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

    private boolean Si() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();

        if (match(JavaLexer.IF)) {
            if (Comparacion()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Enunciados()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
                            generator.connectSi(tupleIndex);
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
                if (Comparacion()) {
                    if (match(JavaLexer.LEFT_BRACKET)) {
                        if (Enunciados()) {
                            if (match(JavaLexer.RIGHT_BRACKET)) {
                                generator.connectSi(tupleIndex);
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
                if (Enunciados()) {
                    if (match(JavaLexer.RIGHT_BRACKET)) {
                        return true;
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Comparacion() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.LEFT_PARENTHESIS)) {
            if (Valor()) {
                if (match(JavaLexer.RELATIONAL)) {
                    if (Valor()) {
                        if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                            generator.createTupleComparacion(auxIndex+1);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Mientras() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();

        if (match(JavaLexer.WHILE)) {
            if (Comparacion()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Enunciados()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
                            generator.connectMientras(tupleIndex);
                            return true;
                        }
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Repite() {
        int auxIndex = tokenIndex;
        int tupleIndex = generator.getTuples().size();
        int incrementDecrementIndex;

        if (match(JavaLexer.FOR)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.INT)) {
                    if (match(JavaLexer.IDENTIFIER)) {
                        define();
                        if (match(JavaLexer.EQUALS)) {
                            if (match(JavaLexer.NUM)) {
                                if (match(JavaLexer.SEMICOLON)) {
                                    if (Valor()) {
                                        if (match(JavaLexer.RELATIONAL)) {
                                            if (Valor()) {
                                                if (match(JavaLexer.SEMICOLON)) {
                                                    generator.createTupleComparacion(auxIndex+7);
                                                    incrementDecrementIndex = tokenIndex;
                                                    if (IncrementDecrement()) {
                                                        if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                                            if (match(JavaLexer.LEFT_BRACKET)) {
                                                                if (Enunciados()) {
                                                                    if (match(JavaLexer.RIGHT_BRACKET)) {
                                                                        generator.createTupleAsignacion(incrementDecrementIndex, incrementDecrementIndex+8);
                                                                        generator.connectMientras(tupleIndex);
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

    private boolean basicTypes() {
        return match(JavaLexer.INT) || match(JavaLexer.CHAR) || match(JavaLexer.BOOLEAN) || match(JavaLexer.STR)
                || match(JavaLexer.FLOAT) || match(JavaLexer.DOUBLE);
    }
}
