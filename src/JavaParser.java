import java.util.ArrayList;

public class JavaParser {
    private ArrayList<Token> tokens;
    private int tokenIndex = 0;
    private SyntaxException ex;

    public void analyze(JavaLexer lexer) throws SyntaxException {
        tokens = lexer.getTokens();

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

    private boolean Program() {
        if (match(JavaLexer.PUBLIC)) {
            if (match(JavaLexer.CLASS)) {
                if (match(JavaLexer.IDENTIFIER)) {
                    if (match(JavaLexer.LEFT_BRACKET)) {
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
                                                                    if (match(JavaLexer.RIGHT_BRACKET)) return true;
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

    private boolean Declaracion() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.INT) || match(JavaLexer.CHAR) || match(JavaLexer.BOOLEAN) || match(JavaLexer.STR)
                || match(JavaLexer.FLOAT) || match(JavaLexer.DOUBLE)) {
            if (match(JavaLexer.IDENTIFIER)) {
                if (match(JavaLexer.SEMICOLON)) return true;

                if (match(JavaLexer.EQUALS)) {
                    if (match(JavaLexer.STR) || match(JavaLexer.NUM) || match(JavaLexer.BOOL)) {
                        if (match(JavaLexer.SEMICOLON)) return true;
                    }

                    if (Leer()) return true;
                }
            }
        }

        tokenIndex = auxIndex;


        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.IDENTIFIER)) {
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

        if (tokens.get(tokenIndex).getType().getName().equals(JavaLexer.IDENTIFIER)) {
            if (Asignacion()) return true;
        }

        tokenIndex = auxIndex;

        if (Declaracion()) return true;

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
            if (match(JavaLexer.EQUALS)) {
                if (Leer()) {
                    return true;
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.EQUALS)) {
                if (Expresion()) {
                    if (match(JavaLexer.SEMICOLON)) return true;
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.ARITHMETIC) || match(JavaLexer.PLUS)) {
                if (match(JavaLexer.EQUALS)) {
                    if (Expresion()) {
                        if (match(JavaLexer.SEMICOLON)) return true;
                    }
                }
            }
        }
        tokenIndex = auxIndex;
        return false;
    }

    private boolean Expresion() {
        int auxIndex = tokenIndex;

        if (Valor()) {
            if (match(JavaLexer.ARITHMETIC) || match(JavaLexer.PLUS)) {
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
        return match(JavaLexer.IDENTIFIER) || match(JavaLexer.NUM);
    }

    private boolean Leer() {
        int auxIndex = tokenIndex;
        if (match(JavaLexer.IDENTIFIER)) {
            if (match(JavaLexer.DOT)) {
                if (match(JavaLexer.READ)) {
                    if (match(JavaLexer.SEMICOLON)) return true;
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
                            if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                                if (match(JavaLexer.SEMICOLON)) return true;
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
                        if (match(JavaLexer.SEMICOLON)) return true;
                    }
                }
            }
        }

        tokenIndex = auxIndex;

        if (match(JavaLexer.WRITE)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.IDENTIFIER)) {
                    if (match(JavaLexer.RIGHT_PARENTHESIS)) {
                        if (match(JavaLexer.SEMICOLON)) return true;
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Si() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.IF)) {
            if (Comparacion()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Enunciados()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
                            return true;
                        }
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
                        if (match(JavaLexer.RIGHT_PARENTHESIS)) return true;
                    }
                }
            }
        }

        tokenIndex = auxIndex;
        return false;
    }

    private boolean Mientras() {
        int auxIndex = tokenIndex;

        if (match(JavaLexer.WHILE)) {
            if (Comparacion()) {
                if (match(JavaLexer.LEFT_BRACKET)) {
                    if (Enunciados()) {
                        if (match(JavaLexer.RIGHT_BRACKET)) {
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

        if (match(JavaLexer.FOR)) {
            if (match(JavaLexer.LEFT_PARENTHESIS)) {
                if (match(JavaLexer.INT)) {
                    if (match(JavaLexer.IDENTIFIER)) {
                        if (match(JavaLexer.EQUALS)) {
                            if (match(JavaLexer.NUM)) {
                                if (match(JavaLexer.SEMICOLON)) {
                                    if (Valor()) {
                                        if (match(JavaLexer.RELATIONAL)) {
                                            if (Valor()) {
                                                if (match(JavaLexer.SEMICOLON)) {
                                                    if (match(JavaLexer.IDENTIFIER)) {
                                                        if (match(JavaLexer.PLUS)) {
                                                            if (match(JavaLexer.PLUS)) {
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
}
