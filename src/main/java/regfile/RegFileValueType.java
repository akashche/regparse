package regfile;

import regfile.parser.Token;

import java.util.Arrays;

public enum RegFileValueType {
    REG_SZ("\""),
    REG_BINARY("hex:"),
    REG_DWORD("dword:"),
    REG_QWORD("hex(b):"),
    REG_MULTI_SZ("hex(7):"),
    REG_EXPAND_SZ("hex(2):");

    private final String value;

    RegFileValueType(String value) {
        this.value = value;
    }

    public static RegFileValueType fromString(Token token, String str) {
        return Arrays.stream(RegFileValueType.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RegFileTokenException(token, String.format(
                "Invalid value type specified: %s", str)));
    }
}
