package scope;

public class Symbol implements Type {
    String name;
    Type type;
    Scope scope;

    public Symbol(String name) {
        this.name = name;
    }

    public Symbol(String name, Type type) {
        this(name);
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (type != null) {
            return "<" + getName() + ":" + type + ">";
        }
        return getName();
    }
}
