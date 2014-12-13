package ru.fizteh.fivt.students.Keksozavr.ExternalTreeDepth;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by КОМПЯШКА on 14.12.2014.
 */
public class ExternalSort {
    private int n;
    private int numberoffiles;
    private int maxbuffersize;
    ExternalSort(int n) {
        this.n = n;
        numberoffiles = (int) Math.ceil(Math.sqrt((double) n));
        maxbuffersize = numberoffiles;
    }

    public void sortwithcomp(final Comparator<Pair<Pair<Integer, Integer>, Integer>> cmp, File in, File out) {
        try (DataInputStream fin = new DataInputStream(new FileInputStream(in))) {
            int k = 0;
            for (int file = 0; file < numberoffiles; file++) {
                ArrayList<Pair<Pair<Integer, Integer>, Integer>> buffer = new ArrayList<>();
                for (int i = 0; i < maxbuffersize && k < n; k++, i++) {
                    buffer.add(new Pair<>(new Pair<>(fin.readInt(), fin.readInt()), fin.readInt()));
                }
                if (buffer.size() == 0) { // такое случается
                    numberoffiles = file;
                    break;
                }
                Collections.sort(buffer, cmp);
                try (DataOutputStream fout =
                             new DataOutputStream(new FileOutputStream("__tmp__" + Integer.toString(file) + ".bin"))) {
                    for (Pair<Pair<Integer, Integer>, Integer> item : buffer) {
                        fout.writeInt(item.getKey().getKey());
                        fout.writeInt(item.getKey().getValue());
                        fout.writeInt(item.getValue());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Can't find file");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Can't work with file");
            System.err.println();
            System.exit(1);
        }

        PriorityQueue<Pair<Pair<Pair<Integer, Integer>, Integer>, DataInputStream>> queue =
                new PriorityQueue<>(numberoffiles,
                        new Comparator<Pair<Pair<Pair<Integer, Integer>, Integer>, DataInputStream>>() {
                            public int compare(Pair<Pair<Pair<Integer, Integer>, Integer>, DataInputStream> a,
                                               Pair<Pair<Pair<Integer, Integer>, Integer>, DataInputStream> b) {
                                return cmp.compare(a.getKey(), b.getKey());
                            }
                        });
        for (int i = 0; i < numberoffiles; i++) {
            DataInputStream stream = null;
            try {
                stream = new DataInputStream(new FileInputStream("__tmp__" + Integer.toString(i) + ".bin"));
            } catch (FileNotFoundException e) {
                System.err.println("Can't open file");
                System.exit(1);
            }
            try {
                queue.add(new Pair<>(new Pair<>(new Pair<>(stream.readInt(), stream.readInt()),
                        stream.readInt()), stream));
            } catch (IOException e) {
                System.err.println("Error: bad file");
                System.exit(1);
            }
        }

        try (DataOutputStream fout = new DataOutputStream(new FileOutputStream(out))) {
            for (int i = 0; i < n; i++) {
                Pair<Pair<Pair<Integer, Integer>, Integer>, DataInputStream> it = queue.poll();
                fout.writeInt(it.getKey().getKey().getKey());
                fout.writeInt(it.getKey().getKey().getValue());
                fout.writeInt(it.getKey().getValue());
                DataInputStream stream = it.getValue();
                try {
                    queue.add(new Pair<>(new Pair<>(new Pair<>(stream.readInt(), stream.readInt()),
                            stream.readInt()), stream));
                } catch (IOException e) {
                    stream.close();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Can't find file");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Can't work with file");
            System.exit(1);
        }

    }

}
