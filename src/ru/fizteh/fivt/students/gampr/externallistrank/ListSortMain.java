package ru.fizteh.fivt.students.gampr.externallistrank;

import java.io.*;

public class ListSortMain {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("2 args are needed");
            System.exit(1);
        }
        File fin = new File(args[0]);
        if (!fin.exists() || !fin.isFile()) {
            System.err.println("Input file is bad!");
            System.exit(1);
        }
        File fout = new File(args[1]);
        File dirTmp = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "tmp");
        dirTmp.mkdir();
        ListSort lr = new ListSort(fin, fout, dirTmp);
        lr.go();
        for (File c : dirTmp.listFiles()) {
            c.delete();
        }
        dirTmp.delete();
    }
}
