public class Token {
    private final TokenType type;
    private final String name;

    public Token (TokenType type, String name) {
        this.type = type;
        this.name = name;
    }

    public TokenType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("<%s, \"%s\">", type.getName(), name);
    }
}
