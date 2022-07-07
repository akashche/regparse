package regparse;

public class RegTokenException extends RuntimeException {

    public RegTokenException(Token token, String message) {
        this.token = token;
        this.message = message;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String getMessage() {
        return message;
    }

    private final Token token;
    private final String message;
}
