package regparse;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;

public class RegKey {

    private RegKey(RegRootKey root, ArrayList<String> pathParts) {
       this.root = root;
       this.pathParts = pathParts;
       this.values = new ArrayList<>();
    }

    static RegKey fromToken(Token token) {
        // example: [root\path\to\key]
        String fullPath = token.image.substring(1, token.image.length() - 1);
        String[] parts = fullPath.split("\\\\");
        if (parts.length < 2) {
            throw new RegTokenException(token,
                    "Registry key cannot consist of root only");
        }
        if (parts.length > MAX_KEY_DEPTH) {
            throw new RegTokenException(token, String.format(
                    "Registry key depth: %d exceeds max allowed depth: %d",
                    parts.length, MAX_KEY_DEPTH));
        }
        RegRootKey root = RegRootKey.fromString(token, parts[0]);
        ArrayList<String> pathParts = Arrays.stream(parts).skip(1)
                .peek(p -> {
                    if (p.length() > MAX_KEY_PART_LENGTH) {
                        throw new RegTokenException(token, String.format(
                                "Registry key part length: %d exceeds max allowed length: %d",
                                p.length(), MAX_KEY_PART_LENGTH));
                    }
                })
                .collect(toCollection(ArrayList::new));
        return new RegKey(root, pathParts);
    }

    public void addValue(RegValue value) {
        this.values.add(value);
    }

    // todo: fixme
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RegistryKey:\n");
        sb.append(root).append("\n");
        sb.append(pathParts.stream()
                .collect(joining("\\"))).append("\n");
        sb.append(values).append("\n");
        return sb.toString();
    }

    private final RegRootKey root;
    private final ArrayList<String> pathParts;
    private final ArrayList<RegValue> values;

    private static final int MAX_KEY_PART_LENGTH = 255;
    private static final int MAX_KEY_DEPTH = 512;

}
