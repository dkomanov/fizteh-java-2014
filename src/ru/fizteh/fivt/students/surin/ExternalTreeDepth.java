package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalTreeDepth {
    static Integer root = null;

    static void findRoot(File edges, int n) throws IOException, ClassNotFoundException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        ObjectOutputStream os = new ObjectOutputStream(buf1, ExternalTreeDepth::writeInt);
        BufferedReader inp = new BufferedReader(new FileReader(edges));
        os.write(n + 1);
        String ss;
        while ((ss = inp.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(ss);
                int parent = Integer.valueOf(st.nextToken());
                int vertex = Integer.valueOf(st.nextToken());
                if (vertex != 0) {
                    os.write(vertex);
                }
        }
        os.close();
        new ExternalSorter<Integer>(buf1, buf2, ExternalTreeDepth::readInt, ExternalTreeDepth::writeInt).run();

        FileIterator.Consumer consumer  =
                new FileIterator.Consumer() {
                    int prev = 0;
                    @Override
                    public void apply(Object o, ArrayList out) throws IOException {
                        Integer cur = (Integer) o;
                        if (cur - prev > 1) {
                            ExternalTreeDepth.root = prev + 1;
                        }
                        prev = cur;
                    }
                };

        FileIterator.forEach(new ObjectInputStream(buf2, ExternalTreeDepth::readInt), consumer);
        buf1.delete();
        buf2.delete();
    }

    static void iterate(File parents) throws IOException, ClassNotFoundException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        FileIterator.map(new ObjectInputStream(parents, ExternalTreeDepth::readTriple),
                         new ObjectOutputStream(buf1, ExternalTreeDepth::writeTriple),
                (Object o, ArrayList out) -> {
                    Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                    Integer v = p.first;
                    Integer par = p.second.first;
                    Integer h = p.second.second;
                    out.add(new Pair<>(par, new Pair<>(v, h)));
                });
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, buf2,
                ExternalTreeDepth::readTriple, ExternalTreeDepth::writeTriple).run();
        FileIterator.joinWith(new ObjectInputStream(buf2, ExternalTreeDepth::readTriple),
                                new ObjectInputStream(parents, ExternalTreeDepth::readTriple),
                                new ObjectOutputStream(buf1, ExternalTreeDepth::writeTriple),
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
                ExternalTreeDepth::readTriple, ExternalTreeDepth::writeTriple).run();
        buf1.delete();
        buf2.delete();
    }

    static void writeInt(Object value, OutputStream os) throws IOException {
        int n = (Integer) value;
        byte[] buf = new byte[] {
                (byte) (n >> 24),
                (byte) (n >> 16),
                (byte) (n >> 8),
                (byte) n};
        os.write(buf);
    }

    static int readInt(InputStream is) throws IOException {
        byte[] buf = new byte[4];
        if (is.read(buf) != 4) {
            throw new EOFException("eof");
        }
        return buf[3] + (((int) buf[2]) << 8) + (((int) buf[1]) << 16) + (((int) buf[0]) << 24);
    }

    static void writeTriple(Object o, OutputStream os) throws IOException {
        Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
        writeInt(p.first, os);
        writeInt(p.second.first, os);
        writeInt(p.second.second, os);
    }

    static Pair<Integer, Pair<Integer, Integer>> readTriple(InputStream is) throws IOException {
        return new Pair<>(readInt(is), new Pair<>(readInt(is), readInt(is)));
    }

    static Integer result;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.exit(2);
        }
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        BufferedReader inp = new BufferedReader(new FileReader(infile));
        File parents = File.createTempFile("tree", ".tmp");
        File tmp = File.createTempFile("tree", ".tmp");
        int n = 1;
        ObjectOutputStream os = new ObjectOutputStream(tmp, ExternalTreeDepth::writeTriple);
        String ss;
        while ((ss = inp.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(ss);
            int parent = Integer.valueOf(st.nextToken());
            int vertex = Integer.valueOf(st.nextToken());
            if (vertex != 0) {
                os.write(new Pair<>(vertex, new Pair(parent, 1)));
                n++;
            }
        }
        inp.close();
        findRoot(infile, n);
        assert root != null;
        System.err.println(root);
        os.write(new Pair<>(root, new Pair<>(root, 0)));
        os.close();

        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(tmp, parents,
                    ExternalTreeDepth::readTriple, ExternalTreeDepth::writeTriple).run();
        tmp.delete();

        int d = 1;
        while (d < n) {
            iterate(parents);
            d *= 2;
        }
        result = Integer.MIN_VALUE;
        FileIterator.forEach(
                new ObjectInputStream(parents, ExternalTreeDepth::readTriple),
                (Object o, ArrayList out) -> {
                    Pair<Integer, Pair<Integer, Integer>> trp = (Pair<Integer, Pair<Integer, Integer>>) o;
                    result = Math.max(result, trp.second.second);
                });

        parents.delete();
        PrintWriter outp = new PrintWriter(outfile);
        outp.print(result + 1);
        outp.close();
    }
}
