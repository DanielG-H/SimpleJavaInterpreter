package generator;

import scope.Scope;
import scope.VariableSymbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Leer extends Tuple {
    Token variable;

    public Leer(Token variable, int jt, int jf) {
        super(jt, jf);
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "( " + super.toString() + ", [ " + variable + " ] )";
    }

    @Override
    public int execute(Scope st) {
        String value = "0.0";

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        try {
            value = input.readLine();
        } catch (IOException _) {}

        VariableSymbol v = (VariableSymbol) st.resolve(variable.getName());

        try {
            v.setValue(Float.parseFloat(value));
        } catch (NumberFormatException ex) {
            System.out.println("Error: Numero no valido");
        }

        return jumpTrue;
    }
}
