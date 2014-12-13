package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mike on 12.12.14.
 */
public class IterTools {
    public interface Translator {
        void apply(Object o, ArrayList out);
    }
    public interface Consumer {
        void consume(Object o) throws IOException;
    }
    public interface Joiner {
        Object join(Object fs, Object sc);
    }
    public interface Comparator {
        int compare(Object a, Object b);
    }

    public static void map(ObjectInputStream is,
                                ObjectOutputStream os,
                                    Translator f) throws IOException {
        Object o;
        try {
            while ((o = is.read()) != null) {
                ArrayList buf = new ArrayList();
                f.apply(o, buf);
                for (Object i : buf) {
                    os.write(i);
                }
                buf.clear();
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void forEach(ObjectInputStream is, Consumer f) throws IOException {
        Object o;
        try {
            while ((o = is.read()) != null) {
                f.consume(o);
            }
        } finally {
            is.close();
        }
    }

    public static void joinWith(ObjectInputStream isf, ObjectInputStream iss,
                                        ObjectOutputStream os,
                                        Comparator matcher,
                                        Joiner joiner) throws IOException {
        Object fcur;
        Object fsec = null;
        try {
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
        } finally {
            isf.close();
            iss.close();
            os.close();
        }
    }
}
