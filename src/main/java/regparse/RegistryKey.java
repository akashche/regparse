package regparse;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

public class RegistryKey {

    private RegistryKey(RegistryRoot root, ArrayList<String> pathParts) {
       this.root = root;
       this.pathParts = pathParts;
       this.values = new ArrayList<>();
    }

    static RegistryKey fromToken(Token token) {
        // example: [root\path\to\key]
        String fullPath = token.image.substring(1, token.image.length() - 1);
        String[] parts = fullPath.split("\\\\");
        if (parts.length < 2 || parts.length > MAX_KEY_DEPTH) {
            throw new RuntimeException("Invalid key TODO");
        }
        RegistryRoot root = RegistryRoot.fromString(parts[0]);
        ArrayList<String> pathParts = Arrays.stream(parts).skip(1)
                .peek(p -> {
                    if (p.length() > MAX_KEY_PART_LENGTH) {
                        throw new RuntimeException("TODO");
                    }
                })
                .collect(toCollection(ArrayList::new));
        return new RegistryKey(root, pathParts);
    }

    public void addValue(RegistryValue value) {
        this.values.add(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryKey:\n");
        sb.append(root).append("\n");
        sb.append(pathParts.stream()
                .collect(joining("\\"))).append("\n");
        sb.append(values).append("\n");
        return sb.toString();
    }

    private final RegistryRoot root;
    private final ArrayList<String> pathParts;
    private final ArrayList<RegistryValue> values;

    private static final int MAX_KEY_PART_LENGTH = 255;
    private static final int MAX_KEY_DEPTH = 512;

}
