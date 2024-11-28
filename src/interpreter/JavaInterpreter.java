package interpreter;

import generator.FinPrograma;
import generator.Tuple;
import scope.Scope;

import java.util.ArrayList;

public class JavaInterpreter {
    Scope st;

    public JavaInterpreter(Scope st) {
        this.st = st;
    }

    public void interpret(ArrayList<Tuple> tuples) {
        int tupleIndex = 0;
        Tuple t = tuples.getFirst();

        do {
            tupleIndex = t.execute(st);
            t = tuples.get(tupleIndex);
        } while (!(t instanceof FinPrograma));
    }
}
