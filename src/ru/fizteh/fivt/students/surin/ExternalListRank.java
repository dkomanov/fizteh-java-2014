package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalListRank {

    static void iterate(File fcur) throws IOException, ClassNotFoundException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        FileIterator.map(new ObjectInputStream(fcur, ExternalListRank::readTriple),
                         new ObjectOutputStream(buf1, ExternalListRank::writeTriple),
                (Object o, ArrayList out) -> {
                    Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                    out.add(new Pair(p.second.first, new Pair(p.first, p.second.second)));
                }
        );
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, buf2,
                ExternalListRank::readTriple, ExternalListRank::writeTriple).run();
        FileIterator.joinWith(new ObjectInputStream(buf2, ExternalListRank::readTriple),
                              new ObjectInputStream(fcur, ExternalListRank::readTriple),
                              new ObjectOutputStream(buf1, ExternalListRank::writeTriple),
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
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, fcur,
                ExternalListRank::readTriple, ExternalListRank::writeTriple).run();
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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.exit(2);
        }
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        BufferedReader inp = new BufferedReader(new FileReader(infile));
        PrintWriter outp = new PrintWriter(outfile);
        File par = File.createTempFile("list-temp", ".tmp");
        File buf = File.createTempFile("list-temp", ".tmp");
        int n = 0;
        ObjectOutputStream b = new ObjectOutputStream(buf, ExternalListRank::writeTriple);
        String line;
        while ((line = inp.readLine()) != null) {
            String[] tokens = line.split(" ");
            int u = Integer.valueOf(tokens[0]);
            int v = Integer.valueOf(tokens[1]);
            b.write(new Pair<>(u, v == 0 ? new Pair<>(u, 0) : new Pair<>(v , 1)));
            n++;
        }
        b.close();

        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf, par,
                ExternalListRank::readTriple, ExternalListRank::writeTriple).run();

        int d = 1;

        while (d < n) {
            iterate(par);
            d *= 2;
        }

        FileIterator.map(new ObjectInputStream(par, ExternalListRank::readTriple),
                         new ObjectOutputStream(buf, ExternalListRank::writeTriple),
                (Object o, ArrayList out) -> {
                    Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) o;
                    out.add(new Pair(-p.second.second, new Pair(p.first, 0)));
                }
                );
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf, par,
                ExternalListRank::readTriple, ExternalListRank::writeTriple).run();
        FileIterator.forEach(new ObjectInputStream(par, ExternalListRank::readTriple),
                (Object o, ArrayList out) -> {
                    outp.println(((Pair<Integer, Pair<Integer, Integer>>) o).second.first);
                }
        );
        buf.delete();
        par.delete();
        outp.close();
    }
}
