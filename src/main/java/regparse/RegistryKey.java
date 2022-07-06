package regparse;

import java.util.ArrayList;

public class RegistryKey {
    private final String name;
    private final ArrayList<RegistryValue> values;

    public RegistryKey(String name) {
        if (name.length() > 255) {
            throw new RuntimeException("Invalid key");
        }
        long count = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '\\') {
                count++;
            }
        }
        if (count > 512) {
            throw new RuntimeException("Invalid key");
        }
        this.name = name;
        this.values = new ArrayList<>();
    }

    public void addValue(RegistryValue value) {
        this.values.add(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryKey:\n");
        sb.append(name).append("\n");
        sb.append(values).append("\n");
        return sb.toString();
    }
}
