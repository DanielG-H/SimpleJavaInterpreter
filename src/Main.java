import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws SyntaxException {
        String input = read("src/programtest.txt");
        JavaLexer lexer = new JavaLexer();
        JavaParser parser = new JavaParser();
        lexer.analyze(input);

        System.out.println("*** Analisis lexico ***");
        for (Token t : lexer.getTokens()) {
            System.out.println(t);
        }

        parser.analyze(lexer);
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
