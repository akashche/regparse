package regparse;

import java.util.Arrays;

public enum RegistryType {
    REG_SZ("\""),
    REG_BINARY("hex:"),
    REG_DWORD("dword:"),
    REG_QWORD("hex(b):"),
    REG_MULTI_SZ("hex(7):"),
    REG_EXPAND_SZ("hex(2):");

    private final String value;

    RegistryType(String value) {
        this.value = value;
    }

    public static RegistryType fromString(String str) throws RuntimeException {
        return Arrays.stream(RegistryType.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown value: [" + str + "]"));
    }
}
