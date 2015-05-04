/*
 * Copyright 2015 Yossi Shaul
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

package org.iostreams.streams.out;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream that calculates the bandwidth of the underlying output stream.
 *
 * @author Yossi Shaul
 */
public class BandwidthMonitorOutputStream extends OutputStream {

    private final OutputStream out;
    private final long startTime = System.nanoTime();
    private long totalBytesWritten;

    public BandwidthMonitorOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        totalBytesWritten++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        totalBytesWritten += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        totalBytesWritten += len;
    }

    public long getTotalBytesWritten() {
        return totalBytesWritten;
    }

    public long getBytesPerSec() {
        if (totalBytesWritten == 0) {
            return 0;
        }
        return (totalBytesWritten * 1000000000) / (System.nanoTime() - startTime);
    }
}
