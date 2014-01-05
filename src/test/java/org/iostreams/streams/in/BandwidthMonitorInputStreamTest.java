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
        //.isLessThanOrEqualTo(in.getLength()); // max bandwidth cannot be bigger than number of bytes read?
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void readMultiBytes() throws IOException {
        StringInputStream in = new StringInputStream("treasure");
        BandwidthMonitorInputStream bmis = new BandwidthMonitorInputStream(in);
        assertThat(bmis.getBytesPerSec()).isEqualTo(0);
        byte[] buf = new byte[2];
        while (bmis.read(buf) != -1) ;
        bmis.close();
        assertThat(bmis.getTotalBytesRead()).isEqualTo(in.getLength());
        assertThat(bmis.getBytesPerSec()).isGreaterThan(0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void readWithOffset() throws IOException {
        StringInputStream in = new StringInputStream("treasure");
        BandwidthMonitorInputStream bmis = new BandwidthMonitorInputStream(in);

        byte[] buf = new byte[4];
        bmis.read(buf, 1, 2);
        assertThat(bmis.getTotalBytesRead()).isEqualTo(2);
        assertThat(bmis.getBytesPerSec()).isGreaterThan(0);

        // consume the rest of the stream
        while (bmis.read(buf, 0, 2) != -1) ;
        bmis.close();
        assertThat(bmis.getTotalBytesRead()).isEqualTo(in.getLength()); // we skipped the first byte
        assertThat(bmis.getBytesPerSec()).isGreaterThan(0);
    }
}
