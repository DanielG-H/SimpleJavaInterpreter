import scope.GlobalScope;
import scope.Symbol;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws SyntaxException {
        String input = read("src/programtest.txt");
        JavaLexer lexer = new JavaLexer();
        GlobalScope globalScope = new GlobalScope();
        JavaParser parser = new JavaParser(globalScope);
        lexer.analyze(input);

        System.out.println("\n*** Analisis lexico ***");
        for (Token t : lexer.getTokens()) {
            System.out.println(t);
        }

        System.out.println("\n*** Analisis sintactico ***");
        parser.analyze(lexer);

        System.out.println("\n*** Tabla de simbolos ***");
        for (Symbol s: globalScope.getSymbols().values()) {
            System.out.println(s);
        }
    }

    private static String read(String name) {
        StringBuilder input = new StringBuilder();

        try {
            FileReader reader = new FileReader(name);
            int character;

            while ((character = reader.read()) != -1) {
                input.append((char) character);
            }

            reader.close();
            return input.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
