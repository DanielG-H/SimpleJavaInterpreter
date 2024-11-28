package generator;

import scope.Scope;

public abstract class Tuple {
    protected int jumpTrue, jumpFalse;

    public Tuple(int jt, int jf) {
        this.jumpTrue = jt;
        this.jumpFalse = jf;
    }

    public void setJumpTrue(int jumpTrue) {
        this.jumpTrue = jumpTrue;
    }

    public int getJumpTrue() {
        return jumpTrue;
    }

    public void setJumpFalse(int jumpFalse) {
        this.jumpFalse = jumpFalse;
    }

    public int getJumpFalse() {
        return jumpFalse;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + ", " + jumpTrue + ", " + jumpFalse;
    }

    public abstract int execute(Scope scope);
}
