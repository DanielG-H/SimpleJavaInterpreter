package generator;

import scope.Scope;
import scope.VariableSymbol;

public class Asignacion extends Tuple {
    Token variable, value1, value2, operator;

    public Asignacion(Token variable, Token value, int jt, int jf) {
        super(jt, jf);
        this.variable = variable;
        this.value1 = value;
    }

    public Asignacion(Token variable, Token value, Token operator, int jt, int jf) {
        super(jt, jf);
        this.variable = variable;
        this.value1 = value;
        this.operator = operator;
    }

    public Asignacion(Token variable, Token value1, Token operator, Token value2, int jt, int jf) {
        super(jt, jf);
        this.variable = variable;
        this.value1 = value1;
        this.value2 = value2;
        this.operator = operator;
    }

    @Override
    public String toString() {
        if (operator == null) {
            return "( " + super.toString() + ", [ \"" + variable + ", " + value1 + "\" ] )";
        } else if (value2 == null){
            return "( " + super.toString() + ", [ \"" + variable + ", " + operator  + ", " + operator  + "\" ] )";
        } else {
            return "( " + super.toString() + ", [ " + variable + ", " + value1 + ", " + operator +
                    ", " + value2 + " ] )";
        }
    }

    @Override
    public int execute(Scope st) {
        VariableSymbol v = (VariableSymbol) st.resolve(variable.getName());
        float operator1 = 0, operator2 = 0;

        if (value1.getType().getName().equals("NUM")) {
            operator1 = Float.parseFloat(value1.getName());
        } else {
            operator1 = ((VariableSymbol) st.resolve(value1.getName())).getValue();
        }

        if (value2 != null) {
            if (value2.getType().getName().equals("NUM")) {
                operator2 = Float.parseFloat(value2.getName());
            } else {
                operator2 = ((VariableSymbol) st.resolve(value2.getName())).getValue();
            }
        }

        if (operator == null) {
            v.setValue(Float.parseFloat(value1.getName()));
        } else if (value2 == null) {
            if (operator.getName().equals("+"))  v.setValue(v.getValue() + 1);
            else v.setValue(v.getValue() - 1);
        } else {
            switch (operator.getName()) {
                case "+": v.setValue(operator1 + operator2);
                break;
                case "-": v.setValue(operator1 - operator2);
                break;
                case "*": v.setValue(operator1 * operator2);
                break;
                case "/": {
                    if (operator2 != 0) {
                        v.setValue(operator1 / operator2);
                    } else {
                        System.out.println("Error: Divison entre cero");
                        System.exit(1);
                    }
                }
            }
        }
        return jumpTrue;
    }
}
