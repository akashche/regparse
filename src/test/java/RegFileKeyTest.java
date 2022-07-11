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
import regfile.parser.Token;

import static org.junit.jupiter.api.Assertions.*;

public class RegFileKeyTest {

    @Test
    public void testFromToken() {
        // invalid
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "")));
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE")));
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "HKEY_LOCAL_MACHINE]")));

        // root only
        assertDoesNotThrow(() -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\foo]")));
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE]")));

        // depth check
        assertDoesNotThrow(() -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\" + ("foo\\".repeat(510)) + "foo]")));
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\" + ("foo\\".repeat(511)) + "foo]")));

        // key part length check
        assertDoesNotThrow(() -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\" + ("a".repeat(255)) + "\\foo]")));
        assertThrows(RegFileTokenException.class, () -> RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\" + ("a".repeat(256)) + "\\foo]")));

        // root and parts
        RegFileKey key = RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\foo\\bar]"));
        assertEquals(RegFileRootKey.HKLM, key.getRoot());
        assertEquals(2, key.getPathParts().size());
        assertEquals("foo", key.getPathParts().get(0));
        assertEquals("bar", key.getPathParts().get(1));
    }

    @Test
    public void testAddValue() {
        RegFileKey key = RegFileKey.fromToken(new Token(0,
                "[HKEY_LOCAL_MACHINE\\foo\\bar]"));
        key.addValue(RegFileValue.fromTokens(
                new Token(0, "\"foo1\"="),
                new Token(0, "\""),
                new Token(0, "bar1\"\r\n")));
        key.addValue(RegFileValue.fromTokens(
                new Token(0, "\"foo2\"="),
                new Token(0, "\""),
                new Token(0, "bar2\"\r\n")));
        assertEquals(2, key.getValues().size());
    }
}
