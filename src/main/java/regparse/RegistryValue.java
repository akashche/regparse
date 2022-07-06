package regparse;

public class RegistryValue {
    private final String name;
    private final RegistryType type;
    private final String value;

    public RegistryValue(String name, String value) {
        if (name.length() > 16383) {
            throw new RuntimeException("Invalid value name");
        }
        this.name = name;
        this.type = RegistryType.STRING;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryValue{\n");
        sb.append(name).append("\n");
        sb.append(type).append("\n");
        sb.append(value).append("\n");
        sb.append('}').append("\n");
        return sb.toString();
    }
}
