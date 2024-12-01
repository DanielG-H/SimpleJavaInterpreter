package generator;

import scope.MethodSymbol;
import scope.Scope;

import java.util.ArrayList;

public class InvokeMethod extends Tuple {
    MethodSymbol method;
    int initialTuple;
    ArrayList<Tuple> tuples;

    public InvokeMethod(MethodSymbol method, int initialTuple, ArrayList<Tuple> tuples, int jt, int jf) {
        super(jt, jf);
        this.method = method;
        this.initialTuple = initialTuple;
        this.tuples = tuples;
    }

    @Override
    public String toString() {
        return  "( " + super.toString() + " )";
    }

    @Override
    public int execute(Scope scope) {
        Tuple finalTuple = tuples.get(method.getEndMethodTuple());
        finalTuple.setJumpFalse(initialTuple + 1);
        finalTuple.setJumpTrue(initialTuple + 1);
        return method.getInitMethodTuple();
    }

    public MethodSymbol getMethod() {
        return method;
    }
}
