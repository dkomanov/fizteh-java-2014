package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalSorter<T extends Comparable<T>> {
    private ObjectInputStream<T> is;
    private ObjectOutputStream<T> os;
    private ObjectInputStream.Reader reader;
    private ObjectOutputStream.Writer writer;

    public ExternalSorter(File inp, File outp, ObjectInputStream.Reader reader,
                          ObjectOutputStream.Writer writer) throws FileNotFoundException {
        is = new ObjectInputStream<>(inp, reader);
        os = new ObjectOutputStream<>(outp, writer);
        this.reader = reader;
        this.writer = writer;
        buf.ensureCapacity(blockLim + 10);
    }

    private final int blockLim = 500000;
    ArrayList<T> buf = new ArrayList<>();
    List<File> tmps = new ArrayList<>();

    public void run() throws ClassNotFoundException, IOException {

        FileIterator.forEach(is, (Object o, ArrayList out) -> {
            T x = (T) o;
            buf.add(x);
            if (buf.size() > blockLim) {
                File temp;
                tmps.add(temp = File.createTempFile("sort-temp", ".tmp"));
                buf.sort((T fs, T sc) -> fs.compareTo(sc));
                ObjectOutputStream<T> os = new ObjectOutputStream<T>(temp, writer);
                for (T obj: buf) {
                    os.write(obj);
                }
                os.close();
                buf.clear();
            }
        });
        if (!buf.isEmpty()) {
            File temp;
            tmps.add(temp = File.createTempFile("sort-temp", ".tmp"));
            buf.sort((T fs, T sc) -> fs.compareTo(sc));
            ObjectOutputStream<T> os = new ObjectOutputStream<T>(temp, writer);
            for (T obj: buf) {
                os.write(obj);
            }
            os.close();
            buf.clear();
        }
        ArrayList<ObjectInputStream<T>> inps = new ArrayList<>();
        for (File f: tmps) {
            inps.add(new ObjectInputStream<>(f, reader));
        }
        PriorityQueue<Pair<T, ObjectInputStream<T>>> cur = new PriorityQueue<>();
        for (ObjectInputStream<T> i: inps) {
            T x;
            if ((x = i.read()) != null) {
                cur.add(new Pair<>(x, i));
            } else {
                i.close();
            }
        }
        while (!cur.isEmpty()) {
            Pair<T, ObjectInputStream<T>> e = cur.poll();
            T key = e.first;
            ObjectInputStream<T> inp = e.second;
            os.write(key);
            if ((e.first = inp.read()) != null) {
                cur.remove(key);
                cur.add(e);
            } else {
                inp.close();
            }
        }
        for (ObjectInputStream is: inps) {
            is.close();
        }
        for (File f : tmps) {
            if (!f.delete()) {
                System.out.println("File deletion failed " + f.getAbsolutePath());
            }
        }
        os.close();
    }
}
