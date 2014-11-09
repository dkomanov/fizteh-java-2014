package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Convenience class for outputting to {@link java.io.ByteArrayOutputStream} and duplicating output
 * to given print streams.<br/>
 * When this output stream is closed, assigned print streams are not affected.
 * @see java.io.PrintStream
 */
public class BAOSDuplicator extends ByteArrayOutputStream {
    private final List<PrintStream> streams;

    public BAOSDuplicator(PrintStream... streams) {
        this.streams = Arrays.asList(streams);
    }

    @Override
    public void write(int b) {
        super.write(b);
        streams.forEach(ps -> ps.write(b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        super.write(b, off, len);
        streams.forEach(ps -> ps.write(b, off, len));
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        streams.forEach(PrintStream::flush);
    }
}
