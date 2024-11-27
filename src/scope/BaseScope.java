package scope;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseScope implements Scope {
    Scope enclosingScope;
    Map<String, Symbol> symbols = new LinkedHashMap<>();

    public BaseScope(Scope currentScope) {
        this.enclosingScope = currentScope;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public void define(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol s = symbols.get(name);

        if (s != null) {
            return s;
        }

        if (enclosingScope != null) {
            return enclosingScope.resolve(name);
        }

        return null;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }
}
