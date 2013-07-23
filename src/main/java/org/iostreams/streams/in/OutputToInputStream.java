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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.*;

/**
 * @author Yossi Shaul
 */
public abstract class OutputToInputStream extends InputStream {
    private static final Logger log = LoggerFactory.getLogger(OutputToInputStream.class);

    private final ExecutorService executor;
    private PipedInputStream pipedInputStream;
    private Future result;

    public OutputToInputStream() {
        this(Executors.newSingleThreadExecutor());
    }

    public OutputToInputStream(ExecutorService executor) {
        this.executor = executor;
    }

    protected abstract void write(OutputStream sink) throws IOException;

    @Override
    public int read() throws IOException {
        if (pipedInputStream == null) {
            // lazily init the piped stream and the worker
            pipedInputStream = new PipedInputStream();
            final PipedOutputStream sink = new PipedOutputStream(pipedInputStream);
            Callable worker = new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        write(sink);
                        return null;
                    } finally {
                        try {
                            sink.close();
                        } catch (IOException e) {
                            log.debug("Failed to close piped output stream", e);
                        }
                    }
                }
            };
            result = executor.submit(worker);
        }
        return pipedInputStream.read();
    }

    @Override
    public void close() throws IOException {
        try {
            result.get(120, TimeUnit.SECONDS);  // the results should be ready by now!
        } catch (InterruptedException e) {
            throw new IOException(e);
        } catch (TimeoutException e) {
            throw new IOException(e);
        } catch (ExecutionException e) {
            if (e.getCause() != null) {
                throw new IOException(e.getCause());
            } else {
                throw new IOException(e);
            }
        }
    }
}
