package regfile;

import regfile.parser.Token;

import java.util.regex.Pattern;

public class RegFileValue {
    private RegFileValue(String name, RegFileValueType type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public static RegFileValue fromTokens(Token nameToken, Token typeToken, Token valueToken) {
        String name = nameFromToken(nameToken);
        RegFileValueType type = RegFileValueType.fromString(typeToken, typeToken.image);
        String value = valueFromToken(type, valueToken);
        return new RegFileValue(name, type, value);
    }

    // todo: fixme
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(":");
        sb.append(type).append(":");
        sb.append(value).append("\n");
        return sb.toString();
    }

    private static String nameFromToken(Token token) {
        // example: '"my name"='
        final String name;
        if (DEFAULT_NAME.equals(token.image)) {
            name = "";
        } else {
            name = token.image.substring(DOUBLE_QUOTE.length(),
                    token.image.length() - QUOTED_NAME_SUFFIX.length());
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new RegFileTokenException(token, String.format(
                    "Registry value name length: %d exceeds max allowed length: %d",
                    name.length(), MAX_NAME_LENGTH));
        }
        return name;
    }

    private static String valueFromToken(RegFileValueType type, Token token) {
        // example: 'my string value"<EOL>'
        final int eolLength;
        if (token.image.endsWith(EOL_CR_LF)) {
            eolLength = EOL_CR_LF.length();
        } else if (token.image.endsWith(EOL_LF)) {
            eolLength = EOL_LF.length();
        } else {
            throw new RegFileTokenException(token, "Registry value EOL error");
        }
        final String valueImage = token.image
                .substring(0, token.image.length() - eolLength);
        switch (type) {
            case REG_SZ: // example: 'my string value"'
                return valueImage.substring(0, valueImage.length() - DOUBLE_QUOTE.length());
            case REG_BINARY:
            case REG_MULTI_SZ:
            case REG_EXPAND_SZ: // example: de,ad,be,...,ef,\<EOL>  de,ad
                if (valueImage.contains(EOL_CR_LF)) {
                    return BACKSLASH_EOL_CR_LF_REGEX.matcher(valueImage).replaceAll("");
                } else if (valueImage.contains(EOL_LF)) {
                    return BACKSLASH_EOL_LF_REGEX.matcher(valueImage).replaceAll("");
                } // fall through
            case REG_DWORD:
            case REG_QWORD: // example: 0000002a
                return valueImage;
            default:
                throw new RegFileTokenException(token, String.format(
                        "Registry value type: %d is not supported", type));
        }
    }

    private final String name;
    private final RegFileValueType type;
    private final String value;

    private static final int MAX_NAME_LENGTH = 16383;
    private static final String DEFAULT_NAME = "@=";
    private static final String QUOTED_NAME_SUFFIX = "\"=";
    private static final String EOL_CR_LF = "\r\n";
    private static final String EOL_LF = "\n";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String DOUBLE_SPACE = "  ";
    private static final Pattern BACKSLASH_EOL_CR_LF_REGEX = Pattern.compile("\\\\" + EOL_CR_LF + DOUBLE_SPACE);
    private static final Pattern BACKSLASH_EOL_LF_REGEX = Pattern.compile("\\\\" + EOL_LF + DOUBLE_SPACE);

}
