package org.iostreams.streams.in;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A {@link java.io.FileInputStream} that deletes the underlying file when this file stream is closed.
 */
public class DeleteOnCloseFileInputStream extends FileInputStream {
    private static final Logger log = LoggerFactory.getLogger(DeleteOnCloseFileInputStream.class);

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
        log.debug("Deleting {} on close", file.getAbsolutePath());
        boolean deleted = file.delete();
        log.debug("File deleted: {}", deleted);
    }
}