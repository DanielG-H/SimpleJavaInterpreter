package scope;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodSymbol extends Symbol implements Scope {
    Scope enclosingScope;
    Map<String, Symbol> symbols = new LinkedHashMap<>();
    ArrayList<String> parameterNames = new ArrayList<>();
    int initMethodTuple = 0;
    int endMethodTuple = 0;

    public MethodSymbol(String name, ArrayList<VariableSymbol> orderedArgs, Scope enclosingScope) {
        super(name);
        this.enclosingScope = enclosingScope;

        if (orderedArgs != null) {
            for (VariableSymbol v : orderedArgs) {
                parameterNames.add(v.getName());
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

    public int getInitMethodTuple() {
        return initMethodTuple;
    }

    public void setInitMethodTuple(int initMethodTuple) {
        this.initMethodTuple = initMethodTuple;
    }

    public int getEndMethodTuple() {
        return endMethodTuple;
    }

    public void setEndMethodTuple(int endMethodTuple) {
        this.endMethodTuple = endMethodTuple;
    }

    public ArrayList<String> getParameterNames() {
        return parameterNames;
    }
}
