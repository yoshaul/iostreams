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

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the StringInputStream.
 *
 * @author Yossi Shaul
 */
public class StringInputStreamTest {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Charset UTF16 = Charset.forName("UTF-16");
    private static final Charset ASCII = Charset.forName("US-ASCII");

    @Test
    public void simpleStringUTF8Encoding() throws IOException {
        String str = "Logen Ninefingers";
        StringInputStream in = new StringInputStream(str);

        assertThat(in.getLength()).isEqualTo(str.length());

        List<String> lines = readLines(in, UTF8);
        assertThat(lines).hasSize(1).containsExactly(str);
    }

    @Test
    public void stringASCIIEncoding() throws IOException {
        String str = "Collem West";
        StringInputStream in = new StringInputStream(str, ASCII);

        assertThat(in.getLength()).isEqualTo(str.length());

        List<String> lines = readLines(in, ASCII);
        assertThat(lines).hasSize(1).containsExactly(str);
    }

    @Test
    public void stringUTF16Encoding() throws IOException {
        String str = "Jezal dan Luthar";
        StringInputStream in = new StringInputStream(str, UTF16);

        assertThat(in.getLength()).isEqualTo(str.getBytes(UTF16).length);

        List<String> lines = readLines(in, UTF16);
        assertThat(lines).hasSize(1).containsExactly(str);
    }

    @Test
    public void multiLineStringUTF8Encoding() throws IOException {
        String str = "line 1\nline2\nline   3";
        StringInputStream in = new StringInputStream(str);

        assertThat(in.getLength()).isEqualTo(str.length());

        List<String> lines = readLines(in);
        assertThat(lines).hasSize(3).containsExactly("line 1", "line2", "line   3");
    }

    private List<String> readLines(InputStream in) throws IOException {
        return readLines(in, UTF8);
    }

    private List<String> readLines(InputStream in, Charset charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
        List<String> lines = new ArrayList<String>();
        String line = br.readLine();
        while (line != null) {
            lines.add(line);
            line = br.readLine();
        }
        return lines;
    }
}
