package ru.fizteh.fivt.students.annafedyushkina.TreeDepth;

import java.io.*;
import java.util.Scanner;

public class FindingDepth {

    public static int addAndReturn(int add, String fileName) {
        int a = 0;
        try (Scanner read = new Scanner(new FileReader(fileName))) {
            a = read.nextInt();
        } catch (IOException e) {
            System.err.println("Some problems with file reading =^.^= " + fileName);
            System.exit(1);
        }
        if (add == 0) {
            return a;
        }
        a += add;
        try (PrintWriter write = new PrintWriter(fileName)) {
            write.print(a);
        } catch (IOException e) {
            System.err.println("Some problems with file printing =^.^=");
            System.exit(1);
        }
        return a;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Not enough arguments =^.^=");
            System.exit(1);
        }
        File fileIn = new File(args[0]);
        File fileOut = new File(args[1]);
        if (!(fileIn.exists()) || !(fileIn.isFile()) || !(fileOut.exists()) || !(fileOut.isFile())) {
            System.err.println("Bad input =^.^=");
            System.exit(1);
        }
        int maxNumber = 0; //количество вершин
        int root = 0;
        try (BufferedReader read = new BufferedReader(new FileReader(fileIn))) {
            String str;
            while ((str = read.readLine()) != null) {
                String[] edges = str.split(" ");
                int from = Integer.parseInt(edges[0]);
                int to = Integer.parseInt(edges[1]);
                if (from > maxNumber) {
                    maxNumber = from;
                }
                if (to > maxNumber) {
                    maxNumber = to;
                }
                if (to != 0) {
                    root ^= to;
                    try (PrintWriter parent = new PrintWriter(new File("parent_" + Integer.toString(to)))) {
                        parent.print(from);
                    } catch (IOException e) {
                        System.err.println("Some problems with parent creation =^.^=");
                        System.exit(1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Some problems with file_in =^.^=");
            System.exit(1);
        }
        for (int i = 1; i <= maxNumber; i++) {
            root ^= i;
            try (PrintWriter degree = new PrintWriter(new File("degree_" + Integer.toString(i)))) {
                degree.print(0);
            } catch (IOException e) {
                System.err.println("Some problems with degree creation =^.^=");
                System.exit(1);
            }
        }

        try (BufferedReader read = new BufferedReader(new FileReader(fileIn))) {
            String str;
            while ((str = read.readLine()) != null) {
                String[] edges = str.split(" ");
                int from = Integer.parseInt(edges[0]);
                int to = Integer.parseInt(edges[1]);
                if (to != 0) {
                    int last = addAndReturn(1, "degree_" + Integer.toString(from));
                    try (PrintWriter son = new PrintWriter(
                            new File("son_" + Integer.toString(from) + "_" + Integer.toString(last)))) {
                        son.print(to);
                    } catch (IOException e) {
                        System.err.println("Some problems with sons creation =^.^=");
                        System.exit(1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Some problems with file_in 2 =^.^=");
            System.exit(1);
        }
        //обход и удаление всех файлов
        int vertex = root;
        int maxDepth = 1;
        int depth = 1;
        while (true) {
            String fileName = "degree_" + Integer.toString(vertex);
            int currDegree = addAndReturn(0, fileName);
            if (currDegree == 0 && vertex == root) {
                File f = new File(fileName);
                f.delete();
                break;
            }
            if (currDegree == 0) {
                String parent = "parent_" + Integer.toString(vertex);
                vertex = addAndReturn(0, parent);
                File file1 = new File(fileName);
                file1.delete();
                File file2 = new File(parent);
                file2.delete();
                depth --;
            } else {
                String son = "son_" + Integer.toString(vertex) + "_" + Integer.toString(currDegree);
                addAndReturn(-1, fileName);
                vertex = addAndReturn(0, son);
                File file1 = new File(son);
                file1.delete();
                depth++;
            }
            if (depth > maxDepth) {
                maxDepth = depth;
            }
        }
        try (PrintWriter out = new PrintWriter(fileOut)) {
            out.print(maxDepth);
        } catch (IOException e) {
            System.err.println("Some problems with file_out =^.^=");
            System.exit(1);
        }
    }
}
