package org.iostreams.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for testing.
 *
 * @author Yossi Shaul
 */
public abstract class StreamsTestUtils {

    public static void consumeAndCloseStream(InputStream in) throws IOException {
        try {
            while (in.read() > -1) ;
        } finally {
            in.close();
        }
    }
}
