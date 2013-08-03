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
 * An input stream created on-the-fly from an output stream.
 * The thread writing to the output stream and the thread reading from the input stream should be different. This
 * implementation creates and manages the thread writing to the output stream.
 *
 * @author Yossi Shaul
 */
public abstract class OutputToInputStream extends InputStream {
    private static final Logger log = LoggerFactory.getLogger(OutputToInputStream.class);

    private final ExecutorService executor;
    private PipedInputStream pipedInputStream;
    private Future result;

    /**
     * Create new <code>OutputToInputStream</code>.
     */
    public OutputToInputStream() {
        this(Executors.newSingleThreadExecutor());
    }

    /**
     * Create new <code>OutputToInputStream</code> with provided executor service for the writing thread.
     *
     * @param executor User provided executor to execute the writing thread
     */
    public OutputToInputStream(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Write to the provided output stream. Implementation should close the sink when finished writing.
     *
     * @param sink The provided output stream which is piped as an input stream.
     * @throws IOException On any I/O error or if the connection with the input stream is lost
     */
    protected abstract void write(OutputStream sink) throws IOException;

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        if (pipedInputStream == null) {
            initializePipedStream();
        }
        return pipedInputStream.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (pipedInputStream == null) {
            initializePipedStream();
        }
        return pipedInputStream.read(b, off, len);
    }

    /**
     * Closes the input stream. <p/>
     * If the writer thread has finished by the time close is called and it resulted in exception, this method will
     * throw an <code>IOException</code> wrapping the exception of the writer.
     *
     * @throws IOException On failure to close the stream or wrapped exception from the writer thread.
     */
    @Override
    public void close() throws IOException {
        if (pipedInputStream == null) {
            log.debug("Called close() and piped stream is null");
            return;
        }
        pipedInputStream.close();

        if (result == null) {
            log.debug("Called close() and result is null");
            return;
        }
        if (!result.isDone()) {
            log.debug("Stream closed while writer still running");
            return;
        }

        try {
            // result is done, check and propagate exception
            result.get();
        } catch (InterruptedException e) {
            throw new IOException(e);
        } catch (ExecutionException e) {
            if (e.getCause() != null) {
                throw new IOException(e.getCause());
            } else {
                throw new IOException(e);
            }
        }
    }

    private void initializePipedStream() throws IOException {
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
}
