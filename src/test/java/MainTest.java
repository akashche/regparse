import org.junit.jupiter.api.Test;
import regparse.Main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class MainTest {

    @Test
    public void test1() throws Exception {
        try (InputStream is = MainTest.class.getResourceAsStream("test.reg")) {
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_16);
            Main.parse(reader);
        }
    }
}
