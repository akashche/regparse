package regfile;

import java.io.Reader;
import java.util.List;

import regfile.parser.Parser;

public class Main {
    public static void parse(Reader reader) throws Exception {
        Parser parser = new Parser(reader);
        List<RegFileKey> keys = parser.parse();
        System.out.println(keys);
    }
}
