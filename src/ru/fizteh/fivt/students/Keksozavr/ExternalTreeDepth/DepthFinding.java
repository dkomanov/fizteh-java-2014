package ru.fizteh.fivt.students.Keksozavr.ExternalTreeDepth;

import javafx.util.Pair;

import java.io.*;
import java.util.Comparator;

/**
 * Created by Честный студент on 13.12.2014.
 */
public class DepthFinding {
    int depth = 0;
    final File merged = new File("__merged__.bin");
    final File sortedbyvertex = new File("__sortedbyvertex__.bin");
    final File sortedbyparent = new File("__sortedbyparent__.bin");
    int n; // количесво вершин
    int root; // корень дерева

    private void init(String inputfilename) {
        n = 0;
        root = 0;
        try (DataOutputStream fout = new DataOutputStream(new FileOutputStream(merged))) {
            try (BufferedReader fin = new BufferedReader(new FileReader(inputfilename))) {
                while (true) {
                    String line = fin.readLine();
                    if (line == null || line.length() < 2) {
                        break;
                    }
                    String[] strs = line.split(" ");
                    int v = Integer.parseInt(strs[0]);
                    int to = Integer.parseInt(strs[1]); //считывание, распознание

                    if (v > n) {
                        n = v;
                    /* Ищем количество вершин в дереве.
                     * Любая вершина будет хотя бы один раз как первая во входных данных
                     */
                    }
                    root ^= to;
                /* Корнем является вершина, в которую ничего не ведет.
                 * Так давайте возьмем xor всех чисел, в которые что-то ведет и всех чисел до n. получится как раз индекс корня.
                 */
                    if (to != 0) {
                        fout.writeInt(to); // отвечает за вершину
                        fout.writeInt(v); // отвечает за предка на высоте 2^k, где k - количетсво итераций.
                        fout.writeInt(1); // отвечает за расстояние до предка.
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Input file was not found!");
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Can't read input file!");
                System.exit(1);
            }

            for (int i = 1; i <= n; i++) {
                root ^= i; // окончательно получили корень
            }

            fout.writeInt(root);
            fout.writeInt(root);
            fout.writeInt(0);

            if (n > 1) {
                depth = 1; // инициализировали глубину
            }

        } catch (IOException e) {
            System.err.println("Can't create file");
        }

    }



    private boolean iteration() {
        ExternalSort sorter = new ExternalSort(n);
        sorter.sortwithcomp(new Comparator<Pair<Pair<Integer, Integer>, Integer>>() {
            @Override
            public int compare(Pair<Pair<Integer, Integer>, Integer> o1, Pair<Pair<Integer, Integer>, Integer> o2) {
                return o1.getKey().getKey().compareTo(o2.getKey().getKey());
            }
        }, merged, sortedbyvertex);

        sorter.sortwithcomp(new Comparator<Pair<Pair<Integer, Integer>, Integer>>() {
            @Override
            public int compare(Pair<Pair<Integer, Integer>, Integer> o1, Pair<Pair<Integer, Integer>, Integer> o2) {
                return o1.getKey().getValue().compareTo(o2.getKey().getValue());
            }
        }, merged, sortedbyparent);

        boolean allisrootlinks = true;

        // MERGE
        try (DataInputStream in1 = new DataInputStream(new FileInputStream(sortedbyvertex));
            DataInputStream in2 = new DataInputStream(new FileInputStream(sortedbyparent));
            DataOutputStream out = new DataOutputStream(new FileOutputStream(merged))) {
            Pair<Pair<Integer, Integer>, Integer> a =
                    new Pair<>(new Pair<>(in1.readInt(), in1.readInt()), in1.readInt());
            for (int i = 0; i < n; i++) {
                Pair<Pair<Integer, Integer>, Integer> b =
                        new Pair<>(new Pair<>(in2.readInt(), in2.readInt()), in2.readInt());
                while (a.getKey().getKey() < b.getKey().getValue()) {
                    a = new Pair<>(new Pair<>(in1.readInt(), in1.readInt()), in1.readInt());
                }
                out.writeInt(b.getKey().getKey());
                out.writeInt(a.getKey().getValue());
                out.writeInt(a.getValue() + b.getValue());
                if (a.getValue() + b.getValue() > depth) {
                    depth = a.getValue() + b.getValue();
                }
                if (a.getKey().getValue() != root) {
                    allisrootlinks = false;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Can't open file");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Can't work with file");
            System.exit(1);
        }

        return !allisrootlinks;

    }

    private void solve(String[] args) {
        init(args[0]);

        while (iteration()) {
            /* Итерацией является перенаправление ссылок на предков
             * в два раза выше(либо в корень, если такого предка нет).
             * Так как максимальная высота это n, то переправляя ссылки мы можем максимум log n раз перенаправить ссылки.
             * Изначально все ссылки направлены на непосредственного предка.
             * iteration вовращает false, если все ссылки направлены на корень.
             * Так же iteration пересчитывает максимальную глубину.
             */
        }
        merged.delete();
        sortedbyparent.delete();
        sortedbyvertex.delete();
        for (int i = 0; i < Math.sqrt((double) n); i++) {

            File file = new File("__tmp__" + Integer.toString(i) + ".bin");
            file.delete();
        }
        // Удаление всех файлов

        try (PrintWriter fout = new PrintWriter(new File(args[1]))) {
            fout.print(depth);
        } catch (FileNotFoundException e) {
            System.err.println("Can't open output file");
            System.exit(1);
        }

    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid arguments number");
            System.exit(1);
        }
        final DepthFinding myclass = new DepthFinding();
        myclass.solve(args);
    }


}
