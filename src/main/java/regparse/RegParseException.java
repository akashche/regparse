package regparse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegParseException extends Exception {

    private RegParseException(int beginLine, int beginColumn, int endLine, int endColumn,
                              String parseErrorMessage) {
        super(createMessage(beginLine, beginColumn, endLine, endColumn, parseErrorMessage));
        this.beginLine = beginLine;
        this.beginColumn = beginColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.parseErrorMessage = parseErrorMessage;
    }

    public static RegParseException fromTokenMgrError(TokenMgrError tokenMgrError) {
        Matcher matcher = TOKEN_MGR_ERROR_REGEX.matcher(tokenMgrError.getMessage());
        int beginLine = 0;
        int beginColumn = 0;
        if (matcher.matches()) {
            beginLine = Integer.parseInt(matcher.group(1));
            beginColumn = Integer.parseInt(matcher.group(2));
        }
        return new RegParseException(beginLine, beginColumn, beginLine, beginColumn, tokenMgrError.getMessage());
    }

    public static RegParseException fromParseException(ParseException parseException) {
        return fromToken(parseException.currentToken, parseException.getMessage());
    }

    public static RegParseException fromTokenException(RegTokenException tokenException) {
        return fromToken(tokenException.getToken(), tokenException.getMessage());
    }

    private static RegParseException fromToken(Token token, String parseErrorMessage) {
        return new RegParseException(token.beginLine, token.beginColumn, token.endLine,
                token.endColumn, parseErrorMessage);
    }

    private static String createMessage(int beginLine, int beginColumn, int endLine, int endColumn,
                                              String parseErrorMessage) {
        return new StringBuilder("Registry file parsing error,")
                .append(" begin line: ").append(beginLine).append(",")
                .append(" begin column: ").append(beginColumn).append(",")
                .append(" end line: ").append(endLine).append(",")
                .append(" end column: ").append(endColumn).append(",")
                .append(" message: ").append(parseErrorMessage)
                .toString();
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getBeginColumn() {
        return beginColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public String getParseErrorMessage() {
        return parseErrorMessage;
    }

    private final int beginLine;
    private final int beginColumn;
    private final int endLine;
    private final int endColumn;
    private final String parseErrorMessage;

    private static final Pattern TOKEN_MGR_ERROR_REGEX =
            Pattern.compile("^Lexical error at line (\\d+), column (\\d+)\\..*$");
}
