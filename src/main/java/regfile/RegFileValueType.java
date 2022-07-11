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

package regfile;

import regfile.parser.Token;

import java.util.Arrays;

public enum RegFileValueType {
    REG_SZ("\""),
    REG_BINARY("hex:"),
    REG_DWORD("dword:"),
    REG_QWORD("hex(b):"),
    REG_MULTI_SZ("hex(7):"),
    REG_EXPAND_SZ("hex(2):");

    private final String value;

    RegFileValueType(String value) {
        this.value = value;
    }

    public static RegFileValueType fromString(Token token, String str) {
        return Arrays.stream(RegFileValueType.values())
                .filter(v -> v.value.equals(str))
                .findFirst()
                .orElseThrow(() -> new RegFileTokenException(token, String.format(
                "Invalid value type specified: %s", str)));
    }
}
