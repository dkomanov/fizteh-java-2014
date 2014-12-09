package ru.fizteh.fivt.students.Keksozavr.ExternalListRank;

import java.io.*;

/**
 * Created by КОМПЯШКА on 15.10.2014.
 */

public class ListSorting {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 2) {
            System.err.println("Invalid arguments number");
            System.exit(1);
        }
        try {
            BufferedReader fin = new BufferedReader(new FileReader(new File(args[0])));
            BufferedWriter fout = new BufferedWriter(new FileWriter(new File(args[1])));

            RandomAccessFile buffer = new RandomAccessFile("__tmpfile__.bft", "rw");

            int start = 0; //стартовая вершина
            int curlength = 0; //Текущий размер специального файла

            while (true) {
                String line = fin.readLine();
                if (line == null || line.length() < 2) {
                    break;
                }
                String[] strs  = line.split(" ");
                int v = Integer.parseInt(strs[0]);
                int to = Integer.parseInt(strs[1]); //считывание, распознание

                start ^= v;
                start ^= to;
                /*
                 * стартовая вершина равна xor'у всех чисел во входных данных,
                 * так как только она и 0 встречаются по одному разу, когда остальные - по два.
                 */

                if (curlength <= v - 1) {
                    curlength = (v - 1) * 2;
                    /* Если размер спец.файла слишком маленький для записи в него,
                     * увеличим в два раза(как в vector). Всего увеличение будет не более log(n)
                     */
                    buffer.setLength(curlength * 4);
                }
                buffer.seek((v - 1) * 4);
                buffer.writeInt(to);
            }

            fin.close(); //Больше не понадобится

            int v = start;
            while (v > 0) {
                buffer.seek(4 * (v - 1));
                int to = buffer.readInt();
                fout.write(v + " " + to + "\n"); // Просто пройдемся по списку, стартуя из стартовой вершины.
                v = to;
            }

            fout.close();
            buffer.close();
            File file = new File("__tmpfile__.bft"); //Удалим спец.файл
            file.delete();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }

    }
}
