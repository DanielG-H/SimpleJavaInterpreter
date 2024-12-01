package scope;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodSymbol extends Symbol implements Scope {
    Scope enclosingScope;
    Map<String, Symbol> symbols = new LinkedHashMap<>();

    public MethodSymbol(String name, ArrayList<VariableSymbol> orderedArgs, Scope enclosingScope) {
        super(name);
        this.enclosingScope = enclosingScope;

        if (orderedArgs != null) {
            for (VariableSymbol v : orderedArgs) {
                define(v);
            }
        }
    }

    @Override
    public String getScopeName() {
        return getName();
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
}
