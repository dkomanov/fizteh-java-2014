package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Convenience class for outputting to {@link java.io.ByteArrayOutputStream} and duplicating output
 * to given printstreams.
 * @see java.io.PrintStream
 */
public class BAOSDuplicator extends ByteArrayOutputStream {
    private ByteArrayOutputStream basicOut;
    private List<PrintStream> streams;

    public BAOSDuplicator(PrintStream... streams) {
        basicOut = new ByteArrayOutputStream();
        this.streams = Arrays.asList(streams);
    }

    @Override
    public void write(int b) {
        basicOut.write(b);
        for (PrintStream ps : streams) {
            ps.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        basicOut.write(b, off, len);
        for (PrintStream ps : streams) {
            ps.write(b, off, len);
        }
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        basicOut.writeTo(out);
    }

    @Override
    public void reset() {
        basicOut.reset();
    }

    @Override
    public byte[] toByteArray() {
        return basicOut.toByteArray();
    }

    @Override
    public int size() {
        return basicOut.size();
    }

    @Override
    public String toString() {
        return basicOut.toString();
    }

    @Override
    public String toString(String charsetName) throws UnsupportedEncodingException {
        return basicOut.toString(charsetName);
    }

    @Override
    @Deprecated
    public String toString(int hibyte) {
        return basicOut.toString(hibyte);
    }

    @Override
    public void close() throws IOException {
        basicOut.close();
        for (PrintStream ps : streams) {
            ps.close();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        basicOut.write(b);
        for (PrintStream ps : streams) {
            ps.write(b);
        }
    }

    @Override
    public void flush() throws IOException {
        basicOut.flush();
        for (PrintStream ps : streams) {
            ps.flush();
        }
    }
}
