package scope;

public class VariableSymbol extends Symbol {
    private float value = 0;

    public VariableSymbol(String name, Type type) {
        super(name, type);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }
}
