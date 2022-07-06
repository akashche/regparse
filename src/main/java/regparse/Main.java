package regparse;

import java.io.Reader;
import java.util.List;

public class Main {
    public static void parse(Reader reader) throws Exception {
        RegFile parser = new RegFile(reader);
        List<RegistryKey> keys = parser.parse();
        System.out.println(keys);
    }
}
