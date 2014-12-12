package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalTreeDepth {

    static Integer findRoot(File edges, int n) throws IOException, ClassNotFoundException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(buf1)));
             BufferedReader inp = new BufferedReader(new FileReader(edges))) {
            os.writeObject(n + 1);
            String ss;
            while ((ss = inp.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(ss);
                int parent = Integer.valueOf(st.nextToken());
                int vertex = Integer.valueOf(st.nextToken());
                if (vertex != 0) {
                    os.writeObject(vertex);
                }
            }
        } catch (EOFException e) {
            //
        }
        new ExternalSorter<Integer>(buf1, buf2).run();
        try (ObjectInputStream ip = new ObjectInputStream(new BufferedInputStream(new FileInputStream(buf2)))) {
            Integer prev = 0;
            while (true) {
                Integer cur = (Integer) ip.readObject();
                if (cur - prev > 1) {
                    buf1.delete();
                    buf2.delete();
                    return prev + 1;
                }
                prev = cur;
            }
        } catch (EOFException e) {
            //
        }
        buf1.delete();
        buf2.delete();
        return -1;
    }

    static void iterate(File parents) throws IOException, ClassNotFoundException {
        File buf1 = File.createTempFile("iterate", ".tmp");
        File buf2 = File.createTempFile("iterate", ".tmp");
        try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(buf1)));
             ObjectInputStream ip = new ObjectInputStream(new BufferedInputStream(new FileInputStream(parents)))) {
            while (true) {
                Pair<Integer, Pair<Integer, Integer>> p = (Pair<Integer, Pair<Integer, Integer>>) ip.readObject();
                Integer v = p.first;
                Integer par = p.second.first;
                Integer h = p.second.second;
                os.writeObject(new Pair<>(par, new Pair<>(v, h)));
            }
        } catch (EOFException ignored) {
            //
        }
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, buf2).run();
        try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(buf1)));
             ObjectInputStream ir = new ObjectInputStream(new BufferedInputStream(new FileInputStream(buf2)));
             ObjectInputStream ip = new ObjectInputStream(new BufferedInputStream(new FileInputStream(parents)))) {
            Pair<Integer, Pair<Integer, Integer>> p;
            Integer v = -1;
            Integer par = -1;
            Integer h = -1;
            while (true) {
                Pair<Integer, Pair<Integer, Integer>> request = (Pair<Integer, Pair<Integer, Integer>>) ir.readObject();
                while (request.first >= v) {
                    if (request.first.equals(v)) {
                        os.writeObject(new Pair<>(request.second.first, new Pair<>(par, h + request.second.second)));
                        break;
                    }
                    p = (Pair<Integer, Pair<Integer, Integer>>) ip.readObject();
                    v = p.first;
                    par = p.second.first;
                    h = p.second.second;
                }
            }
        } catch (EOFException ignored) {
            //
        }
        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(buf1, parents).run();
        buf1.delete();
        buf2.delete();
    }

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
        ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmp)));
        String ss;
        while ((ss = inp.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(ss);
            int parent = Integer.valueOf(st.nextToken());
            int vertex = Integer.valueOf(st.nextToken());
            if (vertex != 0) {
                os.writeObject(new Pair<>(vertex, new Pair(parent, 1)));
                n++;
            }
        }
        inp.close();
        Integer root = findRoot(infile, n);
        assert root != -1;
        os.writeObject(new Pair<>(root, new Pair<Integer, Integer>(root, 0)));
        os.close();

        new ExternalSorter<Pair<Integer, Pair<Integer, Integer>>>(tmp, parents).run();
        tmp.delete();

        int d = 1;
        while (d < n) {
            iterate(parents);
            d *= 2;
        }
        int res = 0;
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(parents)))) {
            while (true) {
                Pair<Integer, Pair<Integer, Integer>> t = (Pair<Integer, Pair<Integer, Integer>>) is.readObject();
                res = Math.max(res, t.second.second);
            }
        } catch (EOFException ignored) {
            //
        }
        parents.delete();
        PrintWriter outp = new PrintWriter(outfile);
        outp.print(res + 1);
        outp.close();
    }
}
