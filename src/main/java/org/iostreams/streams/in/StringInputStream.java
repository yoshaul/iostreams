/*
 * Copyright 2013 Yossi Shaul
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.iostreams.streams.in;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * Creates an input stream from a string.
 *
 * @author Yossi Shaul
 */
public class StringInputStream extends ByteArrayInputStream {

    private final int length;

    /**
     * Creates a new string input stream using UTF-8 character set to encode the string.
     *
     * @param str The string to provide the input stream.
     */
    public StringInputStream(@Nonnull String str) {
        this(str, Charset.forName("UTF-8"));
    }

    /**
     * Creates a new string input stream using the given character set to encode the string.
     *
     * @param str     The string to provide the input stream.
     * @param charset Character set to encode the string to bytes.
     */
    public StringInputStream(@Nonnull String str, @Nonnull Charset charset) {
        super(str.getBytes(charset));
        this.length = super.buf.length;
    }

    /**
     * @return The length, in bytes, of the input stream
     */
    public int getLength() {
        return length;
    }
}
