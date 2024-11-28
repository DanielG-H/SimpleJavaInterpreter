package generator;

import java.util.ArrayList;

public class JavaGenerator {
    private final ArrayList<Tuple> tuples = new ArrayList<>();
    ArrayList<Token> tokens;

    public JavaGenerator(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void createTupleAsignacion(int initialIndex, int finalIndex) {
        if (finalIndex - initialIndex == 5) {
            tuples.add(new Asignacion(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tuples.size()+1,
                    tuples.size()+1));
        } else if (finalIndex - initialIndex == 6) {
            tuples.add(new Asignacion(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tokens.get(initialIndex+3),
                    tokens.get(initialIndex+4),
                    tuples.size()+1,
                    tuples.size()+1));

        } else if (finalIndex - initialIndex == 7) {
            tuples.add(new Asignacion(tokens.get(initialIndex),
                    tokens.get(initialIndex),
                    tokens.get(initialIndex + 1),
                    tokens.get(initialIndex + 3),
                    tuples.size() + 1,
                    tuples.size() + 1));
        } else if (finalIndex - initialIndex == 4) {
            tuples.add(new Asignacion(tokens.get(initialIndex),
                    tokens.get(initialIndex),
                    tokens.get(initialIndex+1),
                    tuples.size()+1,
                    tuples.size()+1));
        }
    }

    public void createTupleLeer(int initialIndex) {
        tuples.add(new Leer(tokens.get(initialIndex),
                tuples.size()+1,
                tuples.size()+1));
    }

    public void createTupleEscribir(int initialIndex, int finalIndex) {
        if (finalIndex - initialIndex == 5) {
            tuples.add(new Escribir(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tuples.size()+1,
                    tuples.size()+1));
        } else if (finalIndex - initialIndex == 7) {
            tuples.add(new Escribir(tokens.get(initialIndex),
                    tokens.get(initialIndex+2),
                    tokens.get(initialIndex+4),
                    tuples.size()+1,
                    tuples.size()+1));
        }
    }

    public void createTupleComparacion(int initialIndex) {
        tuples.add(new Comparacion(tokens.get(initialIndex),
                tokens.get(initialIndex+1),
                tokens.get(initialIndex+2),
                tuples.size()+1,
                tuples.size()+1));
    }

    public void createTupleFinPrograma() {
        tuples.add(new FinPrograma());
    }

    // connect

    public void connectSi(int initialTuple) {
        int finalTuple = tuples.size()-1;

        if (initialTuple >= tuples.size() || initialTuple >= finalTuple) {
            return;
        }

        tuples.get(initialTuple).setJumpFalse(finalTuple+1);
    }

    public void connectMientras(int initialTuple) {
        int finalTuple = tuples.size()-1;

        if (initialTuple >= tuples.size() || initialTuple >= finalTuple) {
            return;
        }

        tuples.get(initialTuple).setJumpFalse(finalTuple+1);
        tuples.get(finalTuple).setJumpTrue(initialTuple);
        tuples.get(finalTuple).setJumpFalse(initialTuple);

        for (int i = finalTuple; i > initialTuple; i--) {
            Tuple t = tuples.get(i);
            if (t instanceof Comparacion && (t.getJumpFalse() == (finalTuple + 1))) {
                t.setJumpFalse(initialTuple);
            }
        }
    }

    public ArrayList<Tuple> getTuples() {
        return tuples;
    }
}
