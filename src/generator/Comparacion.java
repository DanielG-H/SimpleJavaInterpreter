package generator;

import scope.Scope;
import scope.VariableSymbol;

public class Comparacion extends Tuple {
    Token value1, value2, operator;

    public Comparacion(Token value1, Token operator, Token value2, int jt, int jf) {
        super(jt, jf);
        this.value1 = value1;
        this.value2 = value2;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "( " + super.toString() + ", [ " + value1 + ", " + operator + ", " + value2 + " ] )";
    }

    @Override
    public int execute(Scope st) {
        float operator1 = 0, operator2 = 0;
        if (value1.getType().getName().equals("NUM")) {
            operator1 = Float.parseFloat(value1.getName());
        } else {
            operator1 = ((VariableSymbol) st.resolve(value1.getName())).getValue();
        }

        if (value2.getType().getName().equals("NUM")) {
            operator2 = Float.parseFloat(value2.getName());
        } else {
            operator2 = ((VariableSymbol) st.resolve(value2.getName())).getValue();
        }

        switch (operator.getName()) {
            case "<": return operator1 < operator2 ? jumpTrue : jumpFalse;
            case "<=": return operator1 <= operator2 ? jumpTrue : jumpFalse;
            case ">": return operator1 > operator2 ? jumpTrue : jumpFalse;
            case ">=": return operator1 >= operator2 ? jumpTrue : jumpFalse;
            case "==": return operator1 == operator2 ? jumpTrue : jumpFalse;
            case "!=": return operator1 != operator2 ? jumpTrue : jumpFalse;
        }
        return jumpTrue;
    }
}
