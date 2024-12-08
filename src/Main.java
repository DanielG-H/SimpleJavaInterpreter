import generator.JavaGenerator;
import generator.Tuple;
import interpreter.JavaInterpreter;
import scope.GlobalScope;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        String input = read("src/test/programtest2.txt");
        JavaLexer lexer = new JavaLexer();
        GlobalScope globalScope = new GlobalScope();
        JavaGenerator generator = new JavaGenerator(lexer.getTokens());
        JavaParser parser = new JavaParser(globalScope, generator);
        lexer.analyze(input);

        parser.analyze(lexer);

        System.out.println("\n*** Tuplas generadas ***");
        for (Tuple t: generator.getTuples()) {
            System.out.println(t);
        }

        System.out.println("\n*** Ejecucion del programa ***\n");

        JavaInterpreter interpreter = new JavaInterpreter(globalScope);
        interpreter.interpret(generator.getTuples());
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
