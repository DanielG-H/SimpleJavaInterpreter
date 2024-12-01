package generator;

import scope.Scope;

public class Method extends Tuple {
    public Method(int jt, int jf) {
        super(jt, jf);
    }

    @Override
    public String toString() {
        return  "( " + super.toString() + " )";
    }

    @Override
    public int execute(Scope scope) {
        return jumpTrue;
    }
}
