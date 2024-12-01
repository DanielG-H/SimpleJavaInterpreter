package generator;

import scope.MethodSymbol;
import scope.Scope;
import scope.VariableSymbol;

import java.util.ArrayList;

public class InvokeMethod extends Tuple {
    MethodSymbol method;
    int initialTuple;
    ArrayList<Tuple> tuples;
    ArrayList<Token> arguments;

    public InvokeMethod(MethodSymbol method, int initialTuple, ArrayList<Tuple> tuples, ArrayList<Token> arguments, int jt, int jf) {
        super(jt, jf);
        this.method = method;
        this.initialTuple = initialTuple;
        this.tuples = tuples;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return  "( " + super.toString() + " )";
    }

    @Override
    public int execute(Scope scope) {

        if (arguments.size() == method.getParameterNames().size()) {
            float operator = 0;
            for (int i = 0; i < arguments.size(); i++) {
                VariableSymbol var = (VariableSymbol) scope.resolve(method.getParameterNames().get(i));
                if (arguments.get(i).getType().getName().equals("NUM")) {
                    operator = Float.parseFloat(arguments.get(i).getName());
                } else {
                    operator = ((VariableSymbol) scope.resolve(arguments.get(i).getName())).getValue();
                }
                var.setValue(operator);
            }
        } else {
            System.out.println("Error: Numero incorrecto de argumentos");
            System.exit(1);
        }

        Tuple finalTuple = tuples.get(method.getEndMethodTuple());
        finalTuple.setJumpFalse(initialTuple + 1);
        finalTuple.setJumpTrue(initialTuple + 1);
        return method.getInitMethodTuple();
    }

    public MethodSymbol getMethod() {
        return method;
    }
}
