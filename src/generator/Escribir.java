package generator;

import scope.Scope;
import scope.VariableSymbol;

public class Escribir extends Tuple {
    Token write, string, variable;

    public Escribir(Token write, Token stringVariable, int jt, int jf) {
        super(jt, jf);

        if (stringVariable.getType().getName().equals("STRING")) {
            this.string = stringVariable;
        } else {
            this.variable = stringVariable;
        }
        this.write = write;
    }

    public Escribir(Token write, Token string, Token variable, int jt, int jf) {
        super(jt, jf);
        this.string = string;
        this.variable = variable;
        this.write = write;
    }

    @Override
    public String toString() {
        if (variable == null) {
            return "( " + super.toString() + ", [ " + string + " ] )";
        }

        if (string == null) {
            return  "( " + super.toString() + ", [ " + variable + " ] )";
        }

        return "( " + super.toString() + ", [ " + string + ", " + variable + " ] )";
    }

    @Override
    public int execute(Scope st) {
        if (string == null) {
            VariableSymbol v = (VariableSymbol) st.resolve(variable.getName());
            float value = v.getValue();
            print(String.valueOf(value));
        } else if (variable == null) {
            print(string.getName());
        } else {
            VariableSymbol v = (VariableSymbol) st.resolve(variable.getName());
            float value = v.getValue();
            print(string.getName() + value);
        }
        return jumpTrue;
    }

    private void print(String value) {
        if (write.getName().equals("System.out.print")) System.out.print(value);
        else System.out.println(value);
    }
}
