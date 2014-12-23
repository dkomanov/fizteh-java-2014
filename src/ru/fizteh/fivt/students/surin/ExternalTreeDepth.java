package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalTreeDepth {

    private static Integer findRoot(File edges, int n) throws IOException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        try (ObjectOutputStream os = new ObjectOutputStream(buf1, ObjectOutputStream::writeInt);
             BufferedReader inp = new BufferedReader(new FileReader(edges))) {
            os.write(n + 1);
            String ss;
            while ((ss = inp.readLine()) != null) {
                int vertex = Integer.valueOf(ss.split(" ")[1]);
                if (vertex != 0) {
                    os.write(vertex);
                }
            }
        } catch (IOException e) {
            if (!buf1.delete() || !buf2.delete()) {
                System.err.println("failed to delete temporary files");
            }
            throw e;
        }
        Integer root = null;
        try {
            new ExternalSorter<Integer>(buf1, buf2, ObjectInputStream::readInt, ObjectOutputStream::writeInt).run();

            try (ObjectInputStream<Integer> inp =
                         new ObjectInputStream<Integer>(buf2, ObjectInputStream::readInt)) {
                Integer cur;
                Integer prev = 0;
                while ((cur = inp.read()) != null) {
                    if (cur - prev > 1) {
                        root = prev + 1;
                    }
                    prev = cur;
                }
            } catch (IOException e) {
                throw e;
            }
        } finally {
            if (!buf1.delete() || !buf2.delete()) {
                System.err.println("failed to delete temporary files");
            }
        }
        return root;
    }

    private static void iterate(File parents) throws IOException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        IterTools.map(new ObjectInputStream(parents, ObjectInputStream::readTriple),
                         new ObjectOutputStream(buf1, ObjectOutputStream::writeTriple),
                (Object o, ArrayList out) -> {
                    Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                    Integer v = p.first;
                    Integer par = p.second.first;
                    Integer h = p.second.second;
                    out.add(new Pair<>(par, new Pair<>(v, h)));
                });
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, buf2,
                ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();
        IterTools.joinWith(new ObjectInputStream(buf2, ObjectInputStream::readTriple),
                                new ObjectInputStream(parents, ObjectInputStream::readTriple),
                                new ObjectOutputStream(buf1, ObjectOutputStream::writeTriple),
                (Object a, Object b) -> ((Pair) a).first.compareTo(((Pair) b).first),
                (Object req, Object orig) -> {
                    Pair<Integer, Pair<Integer, Integer>> org = (Pair<Integer, Pair<Integer, Integer>>) orig;
                    Integer v = org.first;
                    Integer par = org.second.first;
                    Integer h = org.second.second;
                    Pair<Integer, Pair<Integer, Integer>> request = (Pair<Integer, Pair<Integer, Integer>>) req;
                    return new Pair<>(request.second.first, new Pair<>(par, h + request.second.second));
                }
        );
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, parents,
                ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();
        buf1.delete();
        buf2.delete();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("2 arguments needed");
            System.exit(2);
        }
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        BufferedReader inp = null;
        try {
            inp = new BufferedReader(new FileReader(infile));
        } catch (IOException e) {
            System.err.println("failed to open input file");
            System.exit(2);
        }
        File parents = null;
        File tmp = null;
        Integer result = null;
        try {
            parents = File.createTempFile("tree", ".tmp");
            tmp = File.createTempFile("tree", ".tmp");
        } catch (IOException e) {
            System.err.println("failed to create temporary files");
            System.exit(2);
        }
        try {
            int n = 1;
            try (ObjectOutputStream os = new ObjectOutputStream(tmp, ObjectOutputStream::writeTriple)) {
                String ss;
                while ((ss = inp.readLine()) != null) {
                    String[] tokens = ss.split(" ");
                    int parent = Integer.valueOf(tokens[0]);
                    int vertex = Integer.valueOf(tokens[1]);
                    if (vertex != 0) {
                        os.write(new Pair<>(vertex, new Pair(parent, 1)));
                        n++;
                    }
                }
                inp.close();
                Integer root = findRoot(infile, n);
                assert root != null;

                os.write(new Pair<>(root, new Pair<>(root, 0)));
            } catch (IOException e) {
                throw e;
            }

            new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(tmp, parents,
                    ObjectInputStream::readTriple, ObjectOutputStream::writeTriple).run();

            int d = 1;
            while (d < n) {
                iterate(parents);
                d *= 2;
            }
            try (ObjectInputStream<Pair<Integer, Pair<Integer, Integer>>> is =
                         new ObjectInputStream(parents, ObjectInputStream::readTriple)) {
                result = Integer.MIN_VALUE;
                Pair<Integer, Pair<Integer, Integer>> trp;
                while ((trp = is.read()) != null) {
                    result = Math.max(result, trp.second.second);
                }
            } catch (IOException e) {
                throw e;
            }
            try (PrintWriter outp = new PrintWriter(outfile)) {
                outp.print(result + 1);
            } catch (IOException e) {
                throw e;
            }
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getCause());
        } finally {
            if (parents != null && !parents.delete()) {
                System.err.println("failed to delete temporary file");
            }
            if (tmp != null && !tmp.delete()) {
                System.err.println("failed to delete temporary file");
            }
        }
    }
}
