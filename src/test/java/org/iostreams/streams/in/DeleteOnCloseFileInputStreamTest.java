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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link org.iostreams.streams.in.DeleteOnCloseFileInputStream}.
 *
 * @author Yossi Shaul
 */
public class DeleteOnCloseFileInputStreamTest {

    @Test
    public void checkDeleted() throws IOException {
        File file = File.createTempFile("delete", "me");
        DeleteOnCloseFileInputStream dfis = new DeleteOnCloseFileInputStream(file);
        assertTrue(file.exists());
        dfis.close();
        assertFalse("File should have been deleted after closing the stream", file.exists());
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullFile() throws FileNotFoundException {
        new DeleteOnCloseFileInputStream(null);
    }

    @Test(expected = FileNotFoundException.class)
    public void noSuchFile() throws FileNotFoundException {
        new DeleteOnCloseFileInputStream(new File("no/such/file"));
    }
}
