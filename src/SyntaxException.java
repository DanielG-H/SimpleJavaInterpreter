public class SyntaxException extends Exception {
    public SyntaxException(String message) {
        super(message);
    }

    public SyntaxException(String message, String message2) {
        super("Se esperaba un token '" + message + "' y se encontro '" + message2 + "'");
    }
}
