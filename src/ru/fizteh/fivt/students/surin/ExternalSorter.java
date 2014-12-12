package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalSorter<T extends Serializable & Comparable<T>> {
    private InputStream is;
    private OutputStream os;

    public ExternalSorter(InputStream inp, OutputStream outp) {
        is = inp;
        os = outp;
    }
    public ExternalSorter(File fin, File fout) throws FileNotFoundException {
        is = new FileInputStream(fin);
        os = new FileOutputStream(fout);
    }

    void writeBuffer(ArrayList<T> buf, File f) throws IOException {
        ObjectOutputStream outp = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
        for (T i : buf) {
            outp.writeObject(i);
        }
        outp.close();
        buf.clear();
    }

    private final int blockLim = 500000;
    public void run() throws IOException, ClassNotFoundException {
        ArrayList<File> tmps = new ArrayList<>();
        ArrayList<T> buf = new ArrayList<>();
        ObjectInputStream iis = new ObjectInputStream(new BufferedInputStream(is));
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
        while (true) {
            try {
                T b = (T) iis.readObject();
                buf.add(b);
                File temp;
                if (buf.size() > blockLim) {
                    tmps.add(temp = File.createTempFile("sort-temp", ".tmp"));
                    buf.sort((T fs, T sc) -> fs.compareTo(sc));
                    writeBuffer(buf, temp);
                }
            } catch (EOFException e) {
                if (!buf.isEmpty()) {
                    File temp;
                    tmps.add(temp = File.createTempFile("sort-temp", ".tmp"));
                    buf.sort((T fs, T sc) -> fs.compareTo(sc));
                    writeBuffer(buf, temp);
                }
                break;
            }
        }
        ArrayList<ObjectInputStream> inps = new ArrayList<>();
        for (File f: tmps) {
            inps.add(new ObjectInputStream(new BufferedInputStream(new FileInputStream(f.getAbsolutePath()))));
        }
        PriorityQueue<Pair<T, ObjectInputStream>> cur = new PriorityQueue<>();
        //TreeMap<T, ObjectInputStream> cur = new TreeMap<>();
        for (ObjectInputStream i: inps) {
            try {
                cur.add(new Pair<>((T) i.readObject(), i));
            } catch (EOFException e) {
                i.close();
            }
        }
        while (!cur.isEmpty()) {
            Pair<T, ObjectInputStream> e = cur.poll();
            T key = e.first;
            ObjectInputStream inp = e.second;
            oos.writeObject(key);
            try {
                cur.remove(key);
                e.first = (T) inp.readObject();
            } catch (EOFException err) {
                inp.close();
                continue;
            }
            e.second = inp;
            cur.add(e);
        }
        for (ObjectInputStream is: inps) {
            is.close();
        }
        for (File f : tmps) {
            if (!f.delete()) {
                System.out.println("File deletion failed " + f.getAbsolutePath());
            }
        }
        iis.close();
        oos.close();
    }
}
