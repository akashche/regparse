package regparse;

import java.util.Arrays;

public enum RegistryRoot {
    HKMU("HKEY_WIX_HKMU"),
    HKCR("HKEY_CLASSES_ROOT"),
    HKCU("HKEY_CURRENT_USER"),
    HKLM("HKEY_LOCAL_MACHINE"),
    HKU("HKEY_USERS");

    private final String value;

    RegistryRoot(String value) {
        this.value = value;
    }

    public static RegistryRoot fromString(String str) throws RuntimeException {
        return Arrays.stream(RegistryRoot.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown value: [" + str + "]"));
    }
}
