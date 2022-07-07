package regparse;

import java.util.Arrays;

public enum RegValueType {
    REG_SZ("\""),
    REG_BINARY("hex:"),
    REG_DWORD("dword:"),
    REG_QWORD("hex(b):"),
    REG_MULTI_SZ("hex(7):"),
    REG_EXPAND_SZ("hex(2):");

    private final String value;

    RegValueType(String value) {
        this.value = value;
    }

    public static RegValueType fromString(Token token, String str) {
        return Arrays.stream(RegValueType.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RegTokenException(token, String.format(
                "Invalid value type specified: %s", str)));
    }
}
