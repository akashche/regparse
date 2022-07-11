package regfile;

import java.util.Arrays;

import regfile.parser.Token;

public enum RegFileRootKey {
    HKMU("HKEY_WIX_HKMU"),
    HKCR("HKEY_CLASSES_ROOT"),
    HKCU("HKEY_CURRENT_USER"),
    HKLM("HKEY_LOCAL_MACHINE"),
    HKU("HKEY_USERS");

    private final String value;

    RegFileRootKey(String value) {
        this.value = value;
    }

    public static RegFileRootKey fromString(Token token, String str) {
        return Arrays.stream(RegFileRootKey.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RegFileTokenException(token, String.format(
                        "Invalid root key value specified: %s", str)));
    }
}
