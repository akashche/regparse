/*
 * Copyright 2022 akashche@redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.junit.jupiter.api.Test;
import regfile.*;
import regfile.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    public void testNoHeader() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_no_header.reg"));
    }

    @Test
    public void testNoEol() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_no_eol.reg"));
    }

    @Test
    public void testNoKey() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_no_key.reg"));
    }

    @Test
    public void testMalformedKey() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_malformed_key.reg"));
    }

    @Test
    public void testInvalidRoot() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_invalid_root.reg"));
    }

    @Test
    public void testMalformedValueName() {
        assertThrows(RegFileParseException.class, () -> parseResource(
                "test_fail_malformed_value_name.reg"));
    }

    @Test
    public void testEmpty() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_empty.reg");
        assertEquals(0, keys.size());
    }

    @Test
    public void testSimple() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_simple.reg");
        assertEquals(2, keys.size());
        assertEquals(RegFileRootKey.HKLM, keys.get(0).getRoot());
        assertEquals(2, keys.get(0).getPathParts().size());
        assertEquals("SOFTWARE", keys.get(0).getPathParts().get(0));
        assertEquals("test", keys.get(0).getPathParts().get(1));
        assertEquals(2, keys.get(0).getValues().size());
        assertEquals("test_name1", keys.get(0).getValues().get(0).getName());
        assertEquals(RegFileValueType.REG_SZ, keys.get(0).getValues().get(0).getType());
        assertEquals("test_value1", keys.get(0).getValues().get(0).getValue());
        assertEquals("test_name2", keys.get(0).getValues().get(1).getName());
        assertEquals(RegFileValueType.REG_SZ, keys.get(0).getValues().get(1).getType());
        assertEquals("test_value2", keys.get(0).getValues().get(1).getValue());

        assertEquals(RegFileRootKey.HKLM, keys.get(1).getRoot());
        assertEquals(3, keys.get(1).getPathParts().size());
        assertEquals("SOFTWARE", keys.get(1).getPathParts().get(0));
        assertEquals("test", keys.get(1).getPathParts().get(1));
        assertEquals("keys", keys.get(1).getPathParts().get(2));
        assertEquals(1, keys.get(1).getValues().size());
        assertEquals("test_name3", keys.get(1).getValues().get(0).getName());
        assertEquals(RegFileValueType.REG_SZ, keys.get(1).getValues().get(0).getType());
        assertEquals("test_value3", keys.get(1).getValues().get(0).getValue());
    }

    @Test
    public void testRoots() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_roots.reg");
        assertEquals(5, keys.size());
        assertEquals(RegFileRootKey.HKMU, keys.get(0).getRoot());
        assertEquals(RegFileRootKey.HKCR, keys.get(1).getRoot());
        assertEquals(RegFileRootKey.HKCU, keys.get(2).getRoot());
        assertEquals(RegFileRootKey.HKLM, keys.get(3).getRoot());
        assertEquals(RegFileRootKey.HKU, keys.get(4).getRoot());
    }

    @Test
    public void testKeyNonascii() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_key_nonascii.reg");
        assertEquals(1, keys.size());
        assertEquals(2, keys.get(0).getPathParts().size());
        assertEquals("\u0417\u0434\u0440\u0430\u0432\u0435\u0439\u0442\u0435",
                keys.get(0).getPathParts().get(1));
        assertEquals(1, keys.get(0).getValues().size());
    }

    @Test
    public void testKeySpaces() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_key_spaces.reg");
        assertEquals(1, keys.size());
        assertEquals(3, keys.get(0).getPathParts().size());
        assertEquals("foo bar", keys.get(0).getPathParts().get(1));
        assertEquals("baz", keys.get(0).getPathParts().get(2));
        assertEquals(1, keys.get(0).getValues().size());
    }

    @Test
    public void testKeyBrackets() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_key_brackets.reg");
        assertEquals(1, keys.size());
        assertEquals(3, keys.get(0).getPathParts().size());
        assertEquals("foo]]bar[", keys.get(0).getPathParts().get(1));
        assertEquals("baz", keys.get(0).getPathParts().get(2));
        assertEquals(1, keys.get(0).getValues().size());
    }

    @Test
    public void testValueDefault() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_default.reg");
        assertEquals(1, keys.size());
        assertEquals(1, keys.get(0).getValues().size());
        assertEquals("", keys.get(0).getValues().get(0).getName());
    }

    @Test
    public void testValueNameNonascii() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_name_nonascii.reg");
        assertEquals(1, keys.size());
        assertEquals(1, keys.get(0).getValues().size());
        assertEquals("\u0417\u0434\u0440\u0430\u0432\u0435\u0439\u0442\u0435",
                keys.get(0).getValues().get(0).getName());
    }

    @Test
    public void testValueNameQuotes() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_name_quotes.reg");
        assertEquals(1, keys.size());
        assertEquals(1, keys.get(0).getValues().size());
        assertEquals("test\\\"\\\"_name\\\"", keys.get(0).getValues().get(0).getName());
    }

    @Test
    public void testValueSz() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_sz.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(5, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_SZ, val.getType()));
        assertEquals("value_string_empty", values.get(0).getName());
        assertEquals("", values.get(0).getValue());
        assertEquals("value_string_with_case", values.get(1).getName());
        assertEquals("withCase", values.get(1).getValue());
        assertEquals("value_string_with_quotes", values.get(2).getName());
        assertEquals("with_\\\"quotes\\\"\\\"", values.get(2).getValue());
        assertEquals("value_string_with_spaces", values.get(3).getName());
        assertEquals("with spaces", values.get(3).getValue());
        assertEquals("value_string_nonascii", values.get(4).getName());
        assertEquals("\u0417\u0434\u0440\u0430\u0432\u0435\u0439\u0442\u0435",
                values.get(4).getValue());
    }

    @Test
    public void testValueBinary() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_binary.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(3, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_BINARY, val.getType()));
        assertEquals("value_binary_empty", values.get(0).getName());
        assertEquals("", values.get(0).getValue());
        assertEquals("value_binary", values.get(1).getName());
        assertEquals("de,ad,be,ef", values.get(1).getValue());
        assertEquals("value_binary_long", values.get(2).getName());
        assertEquals(
                "01,23,45,67,89,ab,cd,ef,fe,dc,ba,98,76,54,32,10,12,34," +
                "56,78,90,98,76,54,32,12,34,56,78,90,98,76,54,32,12,34,56,78,90,98,76,54,32," +
                "12,34,56,78,90,98,76,54,32,12,34,56,78,90,98,76,54,32,10",
                values.get(2).getValue());
    }

    @Test
    public void testValueDword() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_dword.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(2, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_DWORD, val.getType()));
        assertEquals("value_dword_zero", values.get(0).getName());
        assertEquals("00000000", values.get(0).getValue());
        assertEquals("value_dword", values.get(1).getName());
        assertEquals("0000002a", values.get(1).getValue());
    }

    @Test
    public void testValueQword() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_qword.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(2, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_QWORD, val.getType()));
        assertEquals("value_qword_zero", values.get(0).getName());
        assertEquals("00,00,00,00,00,00,00,00", values.get(0).getValue());
        assertEquals("value_qword", values.get(1).getName());
        assertEquals("b2,c9,06,2a,b6,2f,c1,4c", values.get(1).getValue());
    }

    @Test
    public void testValueMultiSz() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_multi_sz.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(3, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_MULTI_SZ, val.getType()));
        assertEquals("value_multi_string_empty", values.get(0).getName());
        assertEquals("00,00", values.get(0).getValue());
        assertEquals("value_multi_string", values.get(1).getName());
        assertEquals(
                "74,00,65,00,73,00,74,00,5f,00,6d,00,75,00,6c,00,74," +
                "00,69,00,5f,00,73,00,74,00,72,00,69,00,6e,00,67,00,00,00,00,00",
                values.get(1).getValue());
        assertEquals("value_multi_string_long", values.get(2).getName());
        assertEquals(
                "57,00,68,00,61,00,74,00,20,00,69,00,73,00,20," +
                "00,74,00,68,00,69,00,73,00,3f,00,00,00,54,00,68,00,65,00,20,00,70,00,6c,00," +
                "61,00,63,00,65,00,20,00,74,00,6f,00,20,00,63,00,6f,00,6c,00,6c,00,61,00,62," +
                "00,6f,00,72,00,61,00,74,00,65,00,20,00,6f,00,6e,00,20,00,61,00,6e,00,20,00," +
                "6f,00,70,00,65,00,6e,00,2d,00,73,00,6f,00,75,00,72,00,63,00,65,00,20,00,69," +
                "00,6d,00,70,00,6c,00,65,00,6d,00,65,00,6e,00,74,00,61,00,74,00,69,00,6f,00," +
                "6e,00,20,00,6f,00,66,00,20,00,74,00,68,00,65,00,20,00,4a,00,61,00,76,00,61," +
                "00,20,00,50,00,6c,00,61,00,74,00,66,00,6f,00,72,00,6d,00,2c,00,20,00,53,00," +
                "74,00,61,00,6e,00,64,00,61,00,72,00,64,00,20,00,45,00,64,00,69,00,74,00,69," +
                "00,6f,00,6e,00,2c,00,20,00,61,00,6e,00,64,00,20,00,72,00,65,00,6c,00,61,00," +
                "74,00,65,00,64,00,20,00,70,00,72,00,6f,00,6a,00,65,00,63,00,74,00,73,00,2e," +
                "00,00,00,00,00",
                values.get(2).getValue());
    }

    @Test
    public void testValueExpandSz() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_value_expand_sz.reg");
        assertEquals(1, keys.size());
        List<RegFileValue> values = keys.get(0).getValues();
        assertEquals(2, values.size());
        values.forEach(val ->
                assertEquals(RegFileValueType.REG_EXPAND_SZ, val.getType()));
        assertEquals("value_expandable_string_empty", values.get(0).getName());
        assertEquals("00,00", values.get(0).getValue());
        assertEquals("value_expandable_string", values.get(1).getName());
        assertEquals(
                "65,00,78,00,70,00,61,00,6e,00,64,00,61,00,62," +
                "00,6c,00,65,00,20,00,25,00,4a,00,41,00,56,00,41,00,5f,00,48,00,4f,00,4d,00," +
                "45,00,25,00,20,00,76,00,61,00,6c,00,75,00,65,00,00,00",
                values.get(1).getValue());
    }

    @Test
    public void testAll() throws RegFileParseException {
        List<RegFileKey> keys = parseResource("test_success_all.reg");
        assertEquals(10, keys.size());
        RegFileKey key = keys.get(keys.size() - 1);
        assertEquals(RegFileRootKey.HKLM, key.getRoot());
        assertEquals(3, key.getPathParts().size());
        assertEquals("values", key.getPathParts().get(key.getPathParts().size() - 1));
        assertEquals(23, key.getValues().size());
        assertEquals("", key.getValues().get(0).getName());
        assertEquals(RegFileValueType.REG_SZ, key.getValues().get(0).getType());
        assertEquals("test1", key.getValues().get(0).getValue());
    }


    private static List<RegFileKey> parseResource(String path) throws RegFileParseException {
        try (InputStream is = ParserTest.class.getResourceAsStream(path)) {
            Reader reader = new InputStreamReader(is, StandardCharsets.UTF_16);
            Parser parser = new Parser(reader);
            return parser.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
