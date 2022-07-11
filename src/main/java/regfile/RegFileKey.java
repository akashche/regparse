package regfile;

import java.util.ArrayList;
import java.util.Arrays;

import regfile.parser.Token;

import static java.util.stream.Collectors.toCollection;

public class RegFileKey {

    private RegFileKey(RegFileRootKey root, ArrayList<String> pathParts) {
       this.root = root;
       this.pathParts = pathParts;
       this.values = new ArrayList<>();
    }

    public static RegFileKey fromToken(Token token) {
        // example: [root\path\to\key]
        if (!(token.image.startsWith("[") && token.image.endsWith("]"))) {
            throw new RegFileTokenException(token,
                    "Registry key image is invalid");
        }
        String fullPath = token.image.substring(1, token.image.length() - 1);
        String[] parts = fullPath.split("\\\\");
        if (parts.length < 2) {
            throw new RegFileTokenException(token,
                    "Registry key cannot consist of root only");
        }
        if (parts.length > MAX_KEY_DEPTH) {
            throw new RegFileTokenException(token, String.format(
                    "Registry key depth: %d exceeds max allowed depth: %d",
                    parts.length, MAX_KEY_DEPTH));
        }
        RegFileRootKey root = RegFileRootKey.fromString(token, parts[0]);
        ArrayList<String> pathParts = Arrays.stream(parts).skip(1)
                .peek(p -> {
                    if (p.length() > MAX_KEY_PART_LENGTH) {
                        throw new RegFileTokenException(token, String.format(
                                "Registry key part length: %d exceeds max allowed length: %d",
                                p.length(), MAX_KEY_PART_LENGTH));
                    }
                })
                .collect(toCollection(ArrayList::new));
        return new RegFileKey(root, pathParts);
    }

    public void addValue(RegFileValue value) {
        this.values.add(value);
    }

    public RegFileRootKey getRoot() {
        return root;
    }

    public ArrayList<String> getPathParts() {
        return pathParts;
    }

    public ArrayList<RegFileValue> getValues() {
        return values;
    }

    private final RegFileRootKey root;
    private final ArrayList<String> pathParts;
    private final ArrayList<RegFileValue> values;

    private static final int MAX_KEY_PART_LENGTH = 255;
    private static final int MAX_KEY_DEPTH = 512;

}
