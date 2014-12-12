package ru.fizteh.fivt.students.surin;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by mike on 11.12.14.
 */
public class ExternalListRank {
    static class Vertex implements Comparable<Vertex>, Serializable{
        public int vnum;
        public int d;
        Vertex(int vnum, int d) {
            this.vnum = vnum;
            this.d = d;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Vertex)) {
                return false;
            }
            return ((Vertex) o).vnum == vnum && ((Vertex) o).d == d;
        }

        @Override
        public int hashCode() {
            return vnum * 111317 + d;
        }

        @Override
        public int compareTo(Vertex vertex) {
            if (vnum == vertex.vnum) {
                return d - vertex.d;
            }
            return vnum - vertex.vnum;
        }
    }
    static Pair<Integer, Integer> findCloses(File f) throws IOException, ClassNotFoundException {
        File buf = File.createTempFile("list-temp", ".tmp");
        Pair<Integer, Integer> res = new Pair<>(null, 0);
        new ExternalSorter<Pair<Vertex, Vertex>>(f, buf).run();
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(buf)))) {
            Vertex cur = new Vertex(-1, -1);
            ArrayList<Pair<Vertex, Vertex>> curv = new ArrayList<>();
            while (true) {
                Pair<Vertex, Vertex> t = null;
                boolean tobreak = false;
                try {
                    t = (Pair<Vertex, Vertex>) is.readObject();
                } catch (EOFException ignored) {
                    tobreak = true;
                }
                if (tobreak || !t.first.equals(cur)) {
                    if (curv.size() == 1 && cur.vnum != 0) {
                        res.first = cur.vnum;
                    }
                    curv.clear();
                }
                if (tobreak) {
                    break;
                }
                cur = t.first;
                curv.add(t);
            }
        } catch (IOException e) {
            // do nothing
        }
        buf.delete();
        return res;
    }

    static void iterate(File fold, File fnew, int d,
                        Pair<Integer, Integer> closes) throws IOException, ClassNotFoundException {
        Vertex cur = new Vertex(-1, -1);
        ArrayList<Pair<Vertex, Vertex>> curv = new ArrayList<>();
        new ExternalSorter<Pair<Vertex, Vertex>>(fold, fnew).run();
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fnew)));
             ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fold)))) {
            while (true) {
                Pair<Vertex, Vertex> p = null;
                boolean tobreak = false;
                try {
                    p = (Pair<Vertex, Vertex>) is.readObject();
                } catch (EOFException ignored) {
                    tobreak = true;
                }
                if (!tobreak && (p.second.vnum == closes.first || p.second.vnum == closes.second)) {
                    os.writeObject(new Pair<>(p.first, new Vertex(p.second.vnum, p.second.d + d)));
                }
                if (tobreak || (!p.first.equals(cur) && !curv.isEmpty())) {
                    assert curv.size() <= 2;
                    if (curv.size() == 2) {
                        os.writeObject(new Pair<>(curv.get(0).second, curv.get(1).second));
                        os.writeObject(new Pair<>(curv.get(1).second, curv.get(0).second));
                    }
                    curv.clear();
                }
                if (tobreak) {
                    break;
                }
                curv.add(p);
                cur = p.first;
            }
        } catch (EOFException e) {
            //do nothing
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length != 2) {
            System.exit(2);
        }
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        BufferedReader inp = new BufferedReader(new FileReader(infile));
        PrintWriter outp = new PrintWriter(outfile);
        File buf1 = File.createTempFile("list-temp", ".tmp");
        File buf2 = File.createTempFile("list-temp", ".tmp");
        int n = 0;
        try (ObjectOutputStream b = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(buf1)))) {
            String line;
            while ((line = inp.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(line);
                int u = Integer.valueOf(tk.nextToken());
                int v = Integer.valueOf(tk.nextToken());
                b.writeObject(new Pair<>(new Vertex(u, 0), new Vertex(v, 0)));
                b.writeObject(new Pair<>(new Vertex(v, 0), new Vertex(u, 0)));
                //System.err.println(u + " " + v);
                //System.err.flush();
                n++;
            }
        } catch (EOFException ignored) {
            //do nothing
        }
        Pair<Integer, Integer> tails = findCloses(buf1);
        //System.err.println(tails.first + " " + tails.second);
        int d = 1;

        while (d < n) {
            //System.err.println(D);
            iterate(buf1, buf2, d, tails);
            d *= 2;
        }
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(buf1)));
             ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(buf2)))) {
            while (true) {
                Pair<Vertex, Vertex> p = (Pair<Vertex, Vertex>) is.readObject();
                if (p.second.vnum == tails.second && p.first.d == 0) {
                    os.writeObject(new Pair<>(-(d - p.second.d), p.first.vnum));
                }
            }
        } catch (EOFException ignored) {
            // do nothing
        }
        new ExternalSorter<Pair<Integer, Integer>>(buf2, buf1).run();
        //outp.println(tails.second);
        try (ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(new FileInputStream(buf1)))) {
            while (true) {
                Pair<Integer, Integer> p = (Pair<Integer, Integer>) is.readObject();
                outp.println(p.second);
            }

        } catch (EOFException ignored) {
            // do nothing
        }
        //outp.println(tails.first);
        buf1.delete();
        buf2.delete();
        outp.close();
    }
}
