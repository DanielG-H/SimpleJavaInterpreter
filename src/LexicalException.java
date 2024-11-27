public class LexicalException extends RuntimeException {
    public LexicalException(String message) {
        super("El token '" + message + "' es invalido.");
    }
}
