package ru.fizteh.fivt.students.surin;
import java.io.*;

/**
 * Created by mike on 13.12.14.
 */
public class ObjectOutputStream<T> {
    @FunctionalInterface
    public interface Writer{
        void write(Object o, OutputStream os) throws IOException;
    }
    private OutputStream os;
    private Writer writer;

    ObjectOutputStream(File of, Writer writer) throws FileNotFoundException {
        this.os = new BufferedOutputStream(new FileOutputStream(of));
        this.writer = writer;
    }

    ObjectOutputStream(OutputStream os, Writer writer) {
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
