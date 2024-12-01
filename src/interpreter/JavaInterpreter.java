package interpreter;

import generator.EndMethod;
import generator.FinPrograma;
import generator.InvokeMethod;
import generator.Tuple;
import scope.Scope;

import java.util.ArrayList;

public class JavaInterpreter {
    Scope currentScope;

    public JavaInterpreter(Scope st) {
        this.currentScope = st;
    }

    public void interpret(ArrayList<Tuple> tuples) {
        int tupleIndex = 0;
        Tuple t = tuples.getFirst();

        do {
            if (t instanceof InvokeMethod) {
                currentScope = ((InvokeMethod) t).getMethod();
            } else if (t instanceof EndMethod) {
                currentScope = currentScope.getEnclosingScope();
            }
            tupleIndex = t.execute(currentScope);
            t = tuples.get(tupleIndex);
        } while (!(t instanceof FinPrograma));
    }
}
