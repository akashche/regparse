package regparse;

public class RegistryValue {
    private final String name;
    private final RegistryType type;
    private final String value;

    public RegistryValue(String name, RegistryType type, String value) {
        if (name.length() > 16383) {
            throw new RuntimeException("Invalid value name");
        }
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(":");
        sb.append(type).append(":");
        sb.append(value).append("\n");
        return sb.toString();
    }
}
