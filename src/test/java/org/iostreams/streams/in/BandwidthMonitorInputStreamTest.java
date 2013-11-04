package org.iostreams.streams.in;

import org.iostreams.streams.StreamsTestUtils;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the {@link BandwidthMonitorInputStream} class.
 *
 * @author Yossi Shaul
 */
public class BandwidthMonitorInputStreamTest {

    @Test
    public void readSingleByteAtATime() throws IOException {
        StringInputStream in = new StringInputStream("blabla");
        BandwidthMonitorInputStream bmis = new BandwidthMonitorInputStream(in);
        assertThat(bmis.getBytesPerSec()).isEqualTo(0);
        StreamsTestUtils.consumeAndCloseStream(bmis);
        assertThat(bmis.getTotalBytesRead()).isEqualTo(in.getLength());
        assertThat(bmis.getBytesPerSec()).isGreaterThan(0);
        //.isLessThanOrEqualTo(in.getLength()); // max bandwidth cannot be bigger than number of bytes read
    }

}
