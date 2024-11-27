package scope;

public interface Scope {
    String getScopeName();
    Scope getEnclosingScope();
    void define(Symbol symbol);
    Symbol resolve(String name);
}
