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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link java.io.FileInputStream} that deletes the underlying file when this file stream is closed.
 *
 * @author Yossi Shaul
 */
public class DeleteOnCloseFileInputStream extends FileInputStream {
    private static final Logger log = Logger.getLogger(DeleteOnCloseFileInputStream.class.getName());

    private final File file;

    /**
     * Creates and opens a new file input stream.
     *
     * @param file The file to be opened
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some other reason cannot be opened
     */
    public DeleteOnCloseFileInputStream(@Nonnull File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /**
     * Closes this file input stream and deletes the underlying file.
     *
     * @throws IOException If an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        super.close();
        log.log(Level.FINE, "Deleting ''{0}'' on close", file.getAbsolutePath());
        boolean deleted = file.delete();
        log.log(Level.FINE, "{0}  ''{1}''",
                new String[]{deleted ? "Deleted" : "Failed deleting", file.getAbsolutePath()});
    }
}