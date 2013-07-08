package org.iostreams.streams.in;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Yossi Shaul
 */
public class OutputToInputStreamTest {
    private static final Logger log = LoggerFactory.getLogger(OutputToInputStreamTest.class);

    @Test
    public void simpleStringOutToIn() throws IOException {
        InputStream in = new OutputToInputStream() {
            @Override
            public void write(OutputStream sink) throws IOException {
                copyStringToOut("test", sink);
            }
        };
        print(in);
    }

    @Test
    public void testExceptionPropagation() {
        InputStream in = new OutputToInputStream() {
            @Override
            public void write(OutputStream sink) throws IOException {
                throw new IllegalMonitorStateException("propagate this exception");
            }
        };

        try {
            consumeAndCloseStream(in);
            Assert.fail("Should have thrown io exception");
        } catch (IOException e) {
            // expected
            assertThat(e.getMessage()).endsWith("propagate this exception");
            assertThat(e.getCause()).isExactlyInstanceOf(IllegalMonitorStateException.class);
        }
    }

    private void print(InputStream is) throws IOException {
        StringBuilder stb = new StringBuilder();
        byte[] buf = new byte[1024];
        int read;
        while ((read = is.read(buf)) > 0) {
            stb.append(new String(buf, 0, read));
        }
        is.close();
        log.debug("Result: {}", stb.toString());
    }

    private void consumeAndCloseStream(InputStream in) throws IOException {
        try {
            while (in.read() > -1) ;
        } finally {
            in.close();
        }
    }

    private void copyStringToOut(String string, OutputStream os) throws IOException {
        byte[] bytes = string.getBytes();
        for (byte aByte : bytes) {
            os.write(aByte);
        }
        os.close();
    }

}
