package generator;

public class TokenType {
    private final String name; // token category
    private final String pattern; // regular expression defining it

    public TokenType (String name, String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }
}
