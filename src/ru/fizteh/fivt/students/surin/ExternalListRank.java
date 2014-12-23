package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalListRank {

    private static void iterate(File fcur) throws IOException {
        File buf1;
        File buf2;
        try {
            buf1 = File.createTempFile("iterate", ".tmp");
            buf2 = File.createTempFile("iterate", ".tmp");
        } catch (FileNotFoundException e) {
            System.err.println("failed to create temporary files");
            throw new IOException();
        }
        try {
            IterTools.map(new ObjectInputStream(fcur, ObjectInputStream::readTriple),
                    new ObjectOutputStream(buf1, ObjectOutputStream::writeTriple),
                    (Object o, ArrayList out) -> {
                        Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                        out.add(new Pair(p.second.first, new Pair(p.first, p.second.second)));
                    }
            );
            new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, buf2,
                    ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();
            IterTools.joinWith(new ObjectInputStream(buf2, ObjectInputStream::readTriple),
                    new ObjectInputStream(fcur, ObjectInputStream::readTriple),
                    new ObjectOutputStream(buf1, ObjectOutputStream::writeTriple),
                    (Object a, Object b) -> ((Pair) a).first.compareTo(((Pair) b).first),
                    (Object req, Object orig) -> {
                        Pair<Integer, Pair<Integer, Integer>> org = (Pair<Integer, Pair<Integer, Integer>>) orig;
                        Integer par = org.second.first;
                        Integer h = org.second.second;
                        Pair<Integer, Pair<Integer, Integer>> request = (Pair<Integer, Pair<Integer, Integer>>) req;
                        return new Pair<>(request.second.first, new Pair<>(par, h + request.second.second));
                    }
            );
            new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, fcur,
                    ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();
        } finally {
            if (!buf1.delete() || !buf2.delete()) {
                System.err.println("failed to delete temporary files");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("invalid number of args");
            System.exit(2);
        }
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        File par = null;
        File buf = null;
        try (BufferedReader inp = new BufferedReader(new FileReader(infile));
             PrintWriter outp = new PrintWriter(outfile)) {

            try {
                par = File.createTempFile("list-temp", ".tmp");
                buf = File.createTempFile("list-temp", ".tmp");
            } catch (IOException e) {
                System.out.println("failed to create temporary files");
                throw e;
            }
            int n = 0;
            try (ObjectOutputStream b = new ObjectOutputStream(buf, ObjectOutputStream::writeTriple)) {
                String line;
                while ((line = inp.readLine()) != null) {
                    String[] tokens = line.split(" ");
                    int u = Integer.valueOf(tokens[0]);
                    int v = Integer.valueOf(tokens[1]);
                    b.write(new Pair<>(u, v == 0 ? new Pair<>(u, 0) : new Pair<>(v, 1)));
                    n++;
                }
            } catch (IOException e) {
                throw e;
            }

            new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf, par,
                    ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();

            int d = 1;

            while (d < n) {
                iterate(par);
                d *= 2;
            }

            IterTools.map(new ObjectInputStream(par, ObjectInputStream::readTriple),
                    new ObjectOutputStream(buf, ObjectOutputStream::writeTriple),
                    (Object o, ArrayList out) -> {
                        Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                        out.add(new Pair(-p.second.second, new Pair(p.first, 0)));
                    }
            );
            new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf, par,
                    ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();
            IterTools.forEach(new ObjectInputStream(par, ObjectInputStream::readTriple),
                    (Object o) -> outp.println(((Pair<Integer, Pair<Integer, Integer>>) o).second.first)
            );
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } finally {
            if (buf != null && !buf.delete()) {
                System.err.println("failed to delete temporary files");
            }
            if (par != null && !par.delete()) {
                System.err.println("failed to delete temporary file");
            }
        }
    }
}
