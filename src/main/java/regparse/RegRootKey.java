package regparse;

import java.util.Arrays;

public enum RegRootKey {
    HKMU("HKEY_WIX_HKMU"),
    HKCR("HKEY_CLASSES_ROOT"),
    HKCU("HKEY_CURRENT_USER"),
    HKLM("HKEY_LOCAL_MACHINE"),
    HKU("HKEY_USERS");

    private final String value;

    RegRootKey(String value) {
        this.value = value;
    }

    public static RegRootKey fromString(Token token, String str) {
        return Arrays.stream(RegRootKey.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RegTokenException(token, String.format(
                        "Invalid root key value specified: %s", str)));
    }
}
