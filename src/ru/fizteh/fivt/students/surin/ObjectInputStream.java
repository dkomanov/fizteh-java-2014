package ru.fizteh.fivt.students.surin;

import java.io.*;

/**
 * Created by mike on 13.12.14.
 */
public class ObjectInputStream<T> implements AutoCloseable {
    @FunctionalInterface
    public interface Reader {
        Object read(InputStream is) throws IOException;
    }
    private InputStream is;
    private Reader reader;

    ObjectInputStream(InputStream is, Reader reader) {
        this.is = is;
        this.reader = reader;
    }

    public static int readInt(InputStream is) throws IOException {
        byte[] buf = new byte[4];
        if (is.read(buf) != 4) {
            throw new EOFException("eof");
        }
        return buf[3] + (((int) buf[2]) << 8) + (((int) buf[1]) << 16) + (((int) buf[0]) << 24);
    }

    public static Pair<Integer, Pair<Integer, Integer>> readTriple(InputStream is) throws IOException {
        return new Pair<>(readInt(is), new Pair<>(readInt(is), readInt(is)));
    }


    ObjectInputStream(File inf, Reader reader) throws FileNotFoundException {
        this.is = new BufferedInputStream(new FileInputStream(inf));
        this.reader = reader;
    }

    public T read() throws IOException {
        try {
            return (T) this.reader.read(is);
        } catch (EOFException e) {
            return null;
        }
    }

    public void close() throws IOException {
        is.close();
    }

}

