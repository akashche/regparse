
import org.junit.jupiter.api.Test;
import regfile.*;
import regfile.parser.Token;

import static org.junit.jupiter.api.Assertions.*;

public class RegFileValueTest {

    @Test
    public void testFromTokens() {
        // name invalid
        assertDoesNotThrow(() -> RegFileValue.fromTokens(
                new Token(0, "\"foo\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")));
        assertThrows(RegFileTokenException.class, () -> RegFileValue.fromTokens(
                new Token(0, "foo\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")));
        assertThrows(RegFileTokenException.class, () -> RegFileValue.fromTokens(
                new Token(0, "\"foo\""),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")));

        // name length
        assertDoesNotThrow(() -> RegFileValue.fromTokens(
                new Token(0, "\"" + ("a".repeat(16383))  + "\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")));
        assertThrows(RegFileTokenException.class, () -> RegFileValue.fromTokens(
                new Token(0, "\"" + ("a".repeat(16384))  + "\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")));

        // name contents
        assertEquals("foo", RegFileValue.fromTokens(
                new Token(0, "\"foo\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\r\n")
        ).getName());

        // type invalid
        assertThrows(RegFileTokenException.class,() -> RegFileValue.fromTokens(
                new Token(0, "\"foo\"="),
                new Token(0, "fail"),
                new Token(0, "bar\"\r\n")));

        // value invalid
        assertThrows(RegFileTokenException.class, () -> RegFileValue.fromTokens(
                new Token(0, "\"foo\"="),
                new Token(0, "\""),
                new Token(0, "bar\"")));

        // value default
        assertDoesNotThrow(() -> RegFileValue.fromTokens(
                new Token(0, "@="),
                new Token(0, "\""),
                new Token(0, "bar\"\n")));

        // value LF EOL
        assertDoesNotThrow(() -> RegFileValue.fromTokens(
                new Token(0, "\"foo\"="),
                new Token(0, "\""),
                new Token(0, "bar\"\n")));

        // value REG_SZ
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "\""),
                    new Token(0, "bar\"\r\n"));
            assertEquals(RegFileValueType.REG_SZ, val.getType());
            assertEquals("bar", val.getValue());
        }

        // value REG_BINARY empty
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex:"),
                    new Token(0, "\r\n"));
            assertEquals(RegFileValueType.REG_BINARY, val.getType());
            assertEquals("", val.getValue());
        }

        // value REG_BINARY
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex:"),
                    new Token(0, "de,ad,be,ef\r\n"));
            assertEquals(RegFileValueType.REG_BINARY, val.getType());
            assertEquals("de,ad,be,ef", val.getValue());
        }

        // value REG_BINARY long
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex:"),
                    new Token(0, "de,ad,\\\r\n  be,ef\r\n"));
            assertEquals(RegFileValueType.REG_BINARY, val.getType());
            assertEquals("de,ad,be,ef", val.getValue());
        }

        // value REG_DWORD
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "dword:"),
                    new Token(0, "0000002a\r\n"));
            assertEquals(RegFileValueType.REG_DWORD, val.getType());
            assertEquals("0000002a", val.getValue());
        }

        // value REG_QWORD
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex(b):"),
                    new Token(0, "b2,c9,06,2a,b6,2f,c1,4c\r\n"));
            assertEquals(RegFileValueType.REG_QWORD, val.getType());
            assertEquals("b2,c9,06,2a,b6,2f,c1,4c", val.getValue());
        }

        // value REG_MULTI_SZ
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex(7):"),
                    new Token(0, "de,ad,be,ef,00,00\r\n"));
            assertEquals(RegFileValueType.REG_MULTI_SZ, val.getType());
            assertEquals("de,ad,be,ef,00,00", val.getValue());
        }

        // value REG_MULTI_SZ long
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex(7):"),
                    new Token(0, "de,ad,\\\r\n  be,ef,00,00\r\n"));
            assertEquals(RegFileValueType.REG_MULTI_SZ, val.getType());
            assertEquals("de,ad,be,ef,00,00", val.getValue());
        }

        // value REG_EXPAND_SZ
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex(2):"),
                    new Token(0, "de,ad,be,ef,00,00\r\n"));
            assertEquals(RegFileValueType.REG_EXPAND_SZ, val.getType());
            assertEquals("de,ad,be,ef,00,00", val.getValue());
        }

        // value REG_EXPAND_SZ long
        {
            RegFileValue val = RegFileValue.fromTokens(
                    new Token(0, "\"foo\"="),
                    new Token(0, "hex(2):"),
                    new Token(0, "de,ad,\\\r\n  be,ef,00,00\r\n"));
           assertEquals(RegFileValueType.REG_EXPAND_SZ, val.getType());
            assertEquals("de,ad,be,ef,00,00", val.getValue());
        }
    }
}
