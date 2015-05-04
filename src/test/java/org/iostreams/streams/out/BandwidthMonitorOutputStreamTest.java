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

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests the {@link org.iostreams.streams.out.BandwidthMonitorOutputStream} class.
 *
 * @author Yossi Shaul
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "StatementWithEmptyBody"})
public class BandwidthMonitorOutputStreamTest {

    @Test
    public void writeSingleByteAtATime() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BandwidthMonitorOutputStream bmos = new BandwidthMonitorOutputStream(out);
        // nothing written yet
        assertThat(bmos.getTotalBytesWritten()).isEqualTo(0);
        assertThat(bmos.getBytesPerSec()).isEqualTo(0);

        bmos.write(0);
        assertThat(bmos.getTotalBytesWritten()).isEqualTo(1);
        assertThat(bmos.getBytesPerSec()).isGreaterThan(0);
        // make sure the right amount of bytes were written to the underlying byte array
        assertThat(out.size()).isEqualTo(1);
        assertThat(out.toByteArray()).isEqualTo(new byte[]{0});

        for (int i = 1; i < 10; i++) {
            bmos.write(i);
            assertThat(bmos.getTotalBytesWritten()).isEqualTo(i + 1);
            assertThat(bmos.getBytesPerSec()).isGreaterThan(0);
            assertThat(out.size()).isEqualTo(i + 1);
        }
        assertThat(out.toByteArray()).isEqualTo(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

    }

    @Test
    public void writeMultiBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BandwidthMonitorOutputStream bmos = new BandwidthMonitorOutputStream(out);

        byte[] fibonnaci = {0, 1, 1, 2, 3, 5, 8, 13};
        bmos.write(fibonnaci);
        assertThat(bmos.getTotalBytesWritten()).isEqualTo(fibonnaci.length);
        assertThat(bmos.getBytesPerSec()).isGreaterThan(0);
        // make sure the right amount of bytes were written to the underlying byte array
        assertThat(out.size()).isEqualTo(fibonnaci.length);
        assertThat(out.toByteArray()).isEqualTo(fibonnaci);
    }


    @Test
    public void writeWithOffset() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BandwidthMonitorOutputStream bmos = new BandwidthMonitorOutputStream(out);

        byte[] fibonnaci = {0, 1, 1, 2, 3, 5, 8, 13};

        bmos.write(fibonnaci, 0, 2);
        assertThat(bmos.getTotalBytesWritten()).isEqualTo(2);
        assertThat(bmos.getBytesPerSec()).isGreaterThan(0);
        assertThat(out.size()).isEqualTo(2);
        assertThat(out.toByteArray()).isEqualTo(new byte[]{0, 1});

        bmos.write(fibonnaci, 4, 3);
        assertThat(bmos.getTotalBytesWritten()).isEqualTo(5);
        assertThat(bmos.getBytesPerSec()).isGreaterThan(0);
        assertThat(out.size()).isEqualTo(5);
        assertThat(out.toByteArray()).isEqualTo(new byte[]{0, 1, 3, 5, 8});
    }

}
