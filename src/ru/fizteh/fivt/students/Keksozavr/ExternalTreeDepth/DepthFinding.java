package ru.fizteh.fivt.students.Keksozavr.ExternalTreeDepth;

import java.io.*;
import java.util.Scanner;

/**
 * Created by Хитрый студент on 02.12.2014.
 */
public class DepthFinding {

    static int changenumberfile(String filename, int k) {
        /* функция предназачена для изменения единственного числа, находящегося в файле
         * Если k = 0, то оно прости считается
         * Функция возвращает измененное число
         */
        int num = 0;
        try (Scanner in = new Scanner(new FileReader(filename))) {
            num = in.nextInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (k != 0) {
            num += k;
            try (PrintWriter out = new PrintWriter(filename)) {
                out.print(num);
            } catch (FileNotFoundException e) {
                System.err.println("Can't open file");
                System.exit(1);
            }
        }
        return num;
    }

    static void deletefile(String filename) {
        File file = new File(filename);
        file.delete();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Invalid arguments number");
            System.exit(1);
        }


        int n = 0;
        int root = 0; // корень дерева
        try (BufferedReader fin = new BufferedReader(new FileReader(args[0]))) {
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
                    String filename = "Parent" + Integer.toString(to);
                    try (PrintWriter out = new PrintWriter(new File(filename))) {
                        out.print(v);
                    } catch (FileNotFoundException e) {
                        System.err.println("Can't create file");
                        System.exit(1);
                    }
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

            String filename = "Degree" + Integer.toString(i);
            try (PrintWriter out = new PrintWriter(new File(filename))) {
                out.print(0);
            } catch (FileNotFoundException e) {
                System.err.println("Can't create file");
                System.exit(1);
            }
        }

        try (BufferedReader fin = new BufferedReader(new FileReader(args[0]))) {
            while (true) {
                String line = fin.readLine();
                if (line == null || line.length() < 2) {
                    break;
                }
                String[] strs = line.split(" ");
                int v = Integer.parseInt(strs[0]);
                int to = Integer.parseInt(strs[1]); //считывание, распознание

                if (to != 0) {
                    String filename = "Degree" + Integer.toString(v);
                    int num = changenumberfile(filename, 1);
                    filename = "Neighbor" + Integer.toString(v) + "_" + Integer.toString(num);

                    try (PrintWriter out = new PrintWriter(new File(filename))) {
                        out.print(to);
                    } catch (FileNotFoundException e) {
                        System.err.println("Can't create file");
                        System.exit(1);
                    }
                    /* В этом блоке создаем список смежности
                     * У каждой вершины есть количество соседей
                     * Для каждого соседа есть файл, где содержится номер этого соседа.
                     */

                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Input file was not found!");
        } catch (IOException e) {
            System.err.println("Can't read input file!");
        }

        // Пройдёмся обходом в глубину без стэка
        int v = root;
        int ans = 1;
        int curh = 1; // текущая высота
        while (true) {
            String filename = "Degree" + Integer.toString(v);
            int degree = changenumberfile(filename, 0);
            if (curh > ans) {
                ans = curh;
            }
            if (v == root && degree == 0) {
                deletefile(filename);
                break;
            }
            if (degree == 0) {
                String parentfilename = "Parent" + Integer.toString(v);
                v = changenumberfile(parentfilename, 0); // перешли в родителя
                curh--;

                deletefile(filename);
                deletefile(parentfilename);
            } else {
                String neighborfilename = "Neighbor" + Integer.toString(v) + "_" + Integer.toString(degree);
                v = changenumberfile(neighborfilename, 0); // перешли в ребенка и убили его
                curh++;

                deletefile(neighborfilename);
                changenumberfile(filename, -1);
            }
        }

        try (PrintWriter out = new PrintWriter(new File(args[1]))) {
            out.print(ans);
        } catch (FileNotFoundException e) {
            System.err.println("Can't create/open output file!");
            System.exit(1);
        }

    }
}
