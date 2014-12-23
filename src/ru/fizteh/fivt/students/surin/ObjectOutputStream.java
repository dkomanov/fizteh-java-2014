package ru.fizteh.fivt.students.surin;
import java.io.*;

/**
 * Created by mike on 13.12.14.
 */
public class ObjectOutputStream<T> implements AutoCloseable {
    @FunctionalInterface
    public interface Writer{
        void write(Object o, OutputStream os) throws IOException;
    }
    private OutputStream os;
    private Writer writer;

    public static void writeInt(Object value, OutputStream os) throws IOException {
        int n = (Integer) value;
        byte[] buf = new byte[] {
                (byte) (n >> 24),
                (byte) (n >> 16),
                (byte) (n >> 8),
                (byte) n};
        os.write(buf);
    }

    public static void writeTriple(Object o, OutputStream os) throws IOException {
        Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
        writeInt(p.first, os);
        writeInt(p.second.first, os);
        writeInt(p.second.second, os);
    }

    public ObjectOutputStream(File of, Writer writer) throws FileNotFoundException {
        this.os = new BufferedOutputStream(new FileOutputStream(of));
        this.writer = writer;
    }

    public ObjectOutputStream(OutputStream os, Writer writer) {
        this.os = os;
        this.writer = writer;
    }

    public void write(T x) throws IOException {
        writer.write(x, os);
    }

    public void close() throws IOException {
        os.close();
    }
}
