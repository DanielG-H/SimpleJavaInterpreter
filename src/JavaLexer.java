import generator.Token;
import generator.TokenType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaLexer {
    private final ArrayList<TokenType> types = new ArrayList<>();
    private final ArrayList<Token> tokens = new ArrayList<>();

    public static String NUM = "NUM";
    public static String STRING = "STRING";
    public static String PLUS = "PLUS";
    public static String MINUS = "MINUS";
    public static String ARITHMETIC = "ARITHMETIC";
    public static String RELATIONAL = "RELATIONAL";
    public static String EQUALS = "EQUALS";
    public static String INT = "INT";
    public static String FLOAT = "FLOAT";
    public static String DOUBLE = "DOUBLE";
    public static String STR_ARR = "STRARR";
    public static String VOID = "VOID";
    public static String CLASS = "CLASS";
    public static String PUBLIC = "PUBLIC";
    public static String STATIC = "STATIC";
    public static String NEW = "NEW";
    public static String IF = "IF";
    public static String ELSE = "ELSE";
    public static String FOR = "FOR";
    public static String WHILE = "WHILE";
    public static String LEFT_BRACKET = "LEFTBRACKET";
    public static String RIGHT_BRACKET = "RIGHTBRACKET";
    public static String LEFT_PARENTHESIS = "LEFTPARENTHESIS";
    public static String RIGHT_PARENTHESIS = "RIGHTPARENTHESIS";
    public static String WRITE = "WRITE";
    public static String READ = "READ";
    public static String IN = "IN";
    public static String DOT = "DOT";
    public static String COMMA = "COMMA";
    public static String SEMICOLON = "SEMICOLON";
    public static String MAIN = "MAIN";
    public static String IDENTIFIER = "IDENTIFIER";
    public static String SPACE = "SPACE";
    public static String ERROR = "ERROR";

    public JavaLexer() {
        types.add(new TokenType(NUM, "-?[0-9]+(\\.([0-9]+))?"));
        types.add(new TokenType(STRING, "\".*\""));
        types.add(new TokenType(PLUS, "\\+"));
        types.add(new TokenType(MINUS, "\\-"));
        types.add(new TokenType(ARITHMETIC, "[\\%*/]"));
        types.add(new TokenType(RELATIONAL, "<=|>=|==|<|>|!="));
        types.add(new TokenType(EQUALS, "="));
        types.add(new TokenType(READ, "nextDouble\\(\\)|nextInt\\(\\)|nextFloat\\(\\)|nextLine\\(\\)"));
        types.add(new TokenType(MAIN, "\\bmain\\b"));
        types.add(new TokenType(INT, "\\bint\\b"));
        types.add(new TokenType(FLOAT, "\\bfloat\\b"));
        types.add(new TokenType(DOUBLE, "\\bdouble\\b"));
        types.add(new TokenType(VOID, "\\bvoid\\b"));
        types.add(new TokenType(STR_ARR, "String\\[\\]"));
        types.add(new TokenType(CLASS, "\\bclass\\b"));
        types.add(new TokenType(PUBLIC, "\\bpublic\\b"));
        types.add(new TokenType(STATIC, "\\bstatic\\b"));
        types.add(new TokenType(NEW, "\\bnew\\b"));
        types.add(new TokenType(IF, "\\bif\\b"));
        types.add(new TokenType(ELSE, "\\belse\\b"));
        types.add(new TokenType(FOR, "\\bfor\\b"));
        types.add(new TokenType(WHILE, "\\bwhile\\b"));
        types.add(new TokenType(LEFT_BRACKET, "\\{"));
        types.add(new TokenType(RIGHT_BRACKET, "\\}"));
        types.add(new TokenType(LEFT_PARENTHESIS, "\\("));
        types.add(new TokenType(RIGHT_PARENTHESIS, "\\)"));
        types.add(new TokenType(WRITE, "\\bSystem\\.out\\.print\\b|\\bSystem\\.out\\.println\\b"));
        types.add(new TokenType(IN, "System.in"));
        types.add(new TokenType(DOT, "\\."));
        types.add(new TokenType(COMMA, ","));
        types.add(new TokenType(SEMICOLON, ";"));
        types.add(new TokenType(IDENTIFIER, "[a-zA-Z_][a-zA-Z0-9_]*"));
        types.add(new TokenType(SPACE, "[ \t\f\r\n]+"));
        types.add(new TokenType(ERROR, "[^ \t\f\n]+"));
    }


    public void analyze(String input) throws LexicalException {
        StringBuffer buffer = new StringBuffer();


        for (TokenType tt: types) {
            buffer.append(String.format("|(?<%s>%s)", tt.getName(), tt.getPattern()));
        }

        Pattern p = Pattern.compile(buffer.substring(1));
        Matcher m = p.matcher(input);

        while (m.find()) {
            for (TokenType tt: types) {
                if (m.group(SPACE) != null) {
                    continue;
                }
                else if (m.group(tt.getName()) != null) {
                    if (tt.getName().equals(ERROR)) {
                        throw new LexicalException(m.group(tt.getName()));
                    }

                    String name = m.group(tt.getName());

                    if (tt.getName().equals(STRING)) {
                        name = name.substring(1, name.length()-1);
                    }

                    tokens.add(new Token(tt, name));
                    break;
                }
            }
        }
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}