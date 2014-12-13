package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mike on 12.12.14.
 */
public class FileIterator {
    public interface Consumer {
        void apply(Object o, ArrayList out) throws IOException;
    }
    public interface Joiner {
        Object join(Object fs, Object sc);
    }
    public interface Comparator {
        int compare(Object a, Object b);
    }

    public static void map(ObjectInputStream is,
                                ObjectOutputStream os,
                                    Consumer f) throws IOException, ClassNotFoundException {
        Object o;
        while ((o = is.read()) != null) {
            ArrayList buf = new ArrayList();
            f.apply(o, buf);
            for (Object i : buf) {
                os.write(i);
            }
            buf.clear();
        }
        is.close();
        os.close();
    }

    public static void forEach(ObjectInputStream is, Consumer f) throws IOException {
        ArrayList buf = new ArrayList();
        Object o;
        while ((o = is.read()) != null) {
            f.apply(o, buf);
        }
        is.close();
    }

    public static void joinWith(ObjectInputStream isf, ObjectInputStream iss,
                                        ObjectOutputStream os,
                                        Comparator matcher,
                                        Joiner joiner) throws IOException, ClassNotFoundException {
        Object fcur;
        Object fsec = null;
        while ((fcur = isf.read()) != null) {
            while (fsec == null || matcher.compare(fcur, fsec) >= 0) {
                if (fsec != null && matcher.compare(fcur, fsec) == 0) {
                    os.write(joiner.join(fcur, fsec));
                    break;
                }
                if ((fsec = iss.read()) == null) {
                    break;
                }
            }
        }
        isf.close();
        iss.close();
        os.close();
    }

    public static void joinAndMap(Consumer consumer,
                                  Comparator comparator,
                                  ObjectInputStream is,
                                  ObjectOutputStream os) throws IOException, ClassNotFoundException {
        try {
            ArrayList buf = new ArrayList();
            ArrayList eqsegm = new ArrayList();
            Object last = null;
            while (true) {
                Object o = is.read();
                if (last == null || comparator.compare(last, o) != 0) {
                    consumer.apply(eqsegm, buf);
                    for (Object i: buf) {
                        os.write(i);
                    }
                    eqsegm.clear();
                    buf.clear();
                }
                eqsegm.add(o);
                last = o;
            }
        } catch (EOFException e) {
            //
        }
        is.close();
        os.close();
    }

}
