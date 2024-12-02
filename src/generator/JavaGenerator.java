package generator;

import scope.MethodSymbol;

import java.util.ArrayList;

public class JavaGenerator {
    private final ArrayList<Tuple> tuples = new ArrayList<>();
    ArrayList<Token> tokens;

    public JavaGenerator(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void createTupleAssignment(int initialIndex, int finalIndex) {
        if (finalIndex - initialIndex == 4) { // i = 0  expression
            tuples.add(new Assignment(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tuples.size()+1,
                    tuples.size()+1));
        } else if (finalIndex - initialIndex == 6) { // i = i + 1 expression
            tuples.add(new Assignment(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tokens.get(initialIndex+3),
                    tokens.get(initialIndex+4),
                    tuples.size()+1,
                    tuples.size()+1));

        } else if (finalIndex - initialIndex == 7) { // i += 1 expression
            tuples.add(new Assignment(tokens.get(initialIndex),
                    tokens.get(initialIndex),
                    tokens.get(initialIndex + 1),
                    tokens.get(initialIndex + 3),
                    tuples.size() + 1,
                    tuples.size() + 1));
        } else if (finalIndex - initialIndex == 8){ // i++ expression
            tuples.add(new Assignment(tokens.get(initialIndex),
                    tokens.get(initialIndex),
                    tokens.get(initialIndex+1),
                    tuples.size()+1,
                    tuples.size()+1));
        }
    }

    public void createTupleRead(int initialIndex) {
        tuples.add(new Read(tokens.get(initialIndex),
                tuples.size()+1,
                tuples.size()+1));
    }

    public void createTupleWrite(int initialIndex, int finalIndex) {
        if (finalIndex - initialIndex == 5) {
            tuples.add(new Write(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tuples.size()+1,
                    tuples.size()+1));
        } else if (finalIndex - initialIndex == 7) {
            tuples.add(new Write(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tokens.get(initialIndex+4),
                    tuples.size()+1,
                    tuples.size()+1));
        }
    }

    public void createTupleComparison(int initialIndex) {
        tuples.add(new Comparison(tokens.get(initialIndex),
                tokens.get(initialIndex+1),
                tokens.get(initialIndex+2),
                tuples.size()+1,
                tuples.size()+1));
    }

    public void createMethodTuple() {
        tuples.add(new Method(tuples.size()+1, tuples.size()+1));
    }

    public void createEndMethodTuple() {
        tuples.add(new EndMethod(tuples.size()+1, tuples.size()+1));
    }

    public void createInvokeMethodTuple(int initialTuple, MethodSymbol method, ArrayList<Token> arguments, ArrayList<Tuple> tuples) {
        tuples.add(new InvokeMethod(method, initialTuple, tuples, arguments, tuples.size()+1, tuples.size()+1));
    }

    public void createTupleProgramEnd() {
        tuples.add(new ProgramEnd());
    }

    // connect

    public void connectMethod(int initialTuple, MethodSymbol method) {
        int finalTuple = tuples.size()-1;

        tuples.get(initialTuple).setJumpFalse(finalTuple+1);
        tuples.get(initialTuple).setJumpTrue(finalTuple+1);

        method.setInitMethodTuple(initialTuple + 1);
        method.setEndMethodTuple(finalTuple);
    }

    public void connectIf(int initialTuple) {
        int finalTuple = tuples.size()-1;

        if (initialTuple >= tuples.size() || initialTuple >= finalTuple) {
            return;
        }

        tuples.get(initialTuple).setJumpFalse(finalTuple+1);
    }

    public void connectWhile(int initialTuple) {
        int finalTuple = tuples.size()-1;

        if (initialTuple >= tuples.size() || initialTuple >= finalTuple) {
            return;
        }

        tuples.get(initialTuple).setJumpFalse(finalTuple+1);
        tuples.get(finalTuple).setJumpTrue(initialTuple);
        tuples.get(finalTuple).setJumpFalse(initialTuple);

        for (int i = finalTuple; i > initialTuple; i--) {
            Tuple t = tuples.get(i);
            if (t instanceof Comparison && (t.getJumpFalse() == (finalTuple + 1))) {
                t.setJumpFalse(initialTuple);
            }
        }
    }

    public ArrayList<Tuple> getTuples() {
        return tuples;
    }
}
