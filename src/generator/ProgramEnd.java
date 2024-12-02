package generator;

import scope.Scope;

public class ProgramEnd extends Tuple {
    public ProgramEnd() {
        super(-1, -1);
    }

    @Override
    public String toString() {
        return "( " + super.toString() + ", [ ], " + " )";
    }

    @Override
    public int execute(Scope st) {
        return -1;
    }
}
