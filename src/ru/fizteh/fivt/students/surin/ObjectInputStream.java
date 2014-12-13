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

