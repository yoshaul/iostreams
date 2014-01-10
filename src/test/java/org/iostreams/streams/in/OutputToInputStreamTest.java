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

import org.iostreams.streams.StreamsTestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Unit tests for {@link org.iostreams.streams.in.OutputToInputStream}.
 *
 * @author Yossi Shaul
 */
public class OutputToInputStreamTest {
    private static final Logger log = Logger.getLogger(OutputToInputStreamTest.class.getName());

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
            StreamsTestUtils.consumeAndCloseStream(in);
            Assert.fail("Should have thrown io exception");
        } catch (IOException e) {
            // expected the exception thrown by the writer
            assertThat(e.getMessage()).endsWith("propagate this exception");
            assertThat(e.getCause()).isExactlyInstanceOf(IllegalMonitorStateException.class);
        }
    }

    @Test
    public void writerClosesBeforeReaderStarts() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        OutputToInputStream in = new OutputToInputStream() {
            @Override
            protected void write(OutputStream sink) throws IOException {
                sink.write(7);
                sink.close();
                latch.countDown();
            }
        };

        // call initialize to start the writer thread
        Method initialize = OutputToInputStream.class.getDeclaredMethod("initializePipedStream");
        initialize.setAccessible(true);
        initialize.invoke(in);

        // await for the writer to finish
        latch.await(2, TimeUnit.SECONDS);
        assertThat(latch.getCount()).as("Timed out waiting").isEqualTo(0);

        // try to read
        assertThat(in.read()).as("Read the same byte written").isEqualTo(7);
        assertThat(in.read()).as("Expected end of stream").isEqualTo(-1);
        in.close(); // assure close() finishes without exception
    }

    @Test
    public void inputClosedWithoutReading() throws IOException {
        OutputToInputStream in = new OutputToInputStream() {
            @Override
            protected void write(OutputStream sink) throws IOException {
                // nop
            }
        };

        in.close(); // no exception
    }

    @Test
    public void readerClosesBeforeWriterFinished() throws Exception {
        final CountDownLatch readerDoneLatch = new CountDownLatch(1);
        final CountDownLatch writerDoneLatch = new CountDownLatch(1);
        final Exception[] writerException = new Exception[1];
        OutputToInputStream in = new OutputToInputStream() {
            @Override
            protected void write(OutputStream sink) throws IOException {
                try {
                    sink.write(1);
                    // don't continue until reader closed
                    readerDoneLatch.await(2, TimeUnit.SECONDS);
                    assertThat(readerDoneLatch.getCount()).as("Timed out waiting").isEqualTo(0);
                    sink.write(2);  // should throw an exception since the input stream is now closed
                    Assert.fail("Should have got piped closed exception");
                } catch (Exception e) {
                    writerException[0] = e;
                } finally {
                    writerDoneLatch.countDown();
                }
            }
        };

        // read only one byte, close and release the latch
        in.read();
        in.close();
        readerDoneLatch.countDown();
        writerDoneLatch.await(2, TimeUnit.SECONDS);
        assertThat(writerDoneLatch.getCount()).as("Timed out waiting").isEqualTo(0);
        assertThat(writerException[0]).as("Expecting an exception").isNotNull()
                .isExactlyInstanceOf(IOException.class).hasMessage("Pipe closed");
    }

    private void print(InputStream is) throws IOException {
        StringBuilder stb = new StringBuilder();
        byte[] buf = new byte[1024];
        int read;
        while ((read = is.read(buf)) > 0) {
            stb.append(new String(buf, 0, read));
        }
        is.close();
        log.log(Level.FINE, "Result: {0}", stb.toString());
    }

    private void copyStringToOut(String string, OutputStream os) throws IOException {
        byte[] bytes = string.getBytes();
        for (byte aByte : bytes) {
            os.write(aByte);
        }
        os.close();
    }
}
