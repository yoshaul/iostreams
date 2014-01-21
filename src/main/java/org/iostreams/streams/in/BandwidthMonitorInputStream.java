package org.iostreams.streams.in;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream that calculates the bandwidth of the input stream.
 *
 * @author Yossi Shaul
 */
public class BandwidthMonitorInputStream extends FilterInputStream {

    private final long startTime = System.nanoTime();
    private long totalBytesRead;

    public BandwidthMonitorInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int n = in.read();
        if (n > -1) {
            totalBytesRead++;
        }
        return n;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int n = in.read(b);
        if (n > -1) {
            totalBytesRead += n;
        }
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if (n > -1) {
            totalBytesRead += n;
        }
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        totalBytesRead += skipped;
        return skipped;
    }

    public long getTotalBytesRead() {
        return totalBytesRead;
    }

    public long getBytesPerSec() {
        if (totalBytesRead == 0) {
            return 0;
        }
        return (totalBytesRead * 1000000000) / (System.nanoTime() - startTime);
    }
}
