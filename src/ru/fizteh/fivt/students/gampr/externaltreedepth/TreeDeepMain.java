package ru.fizteh.fivt.students.gampr.externaltreedepth;

import java.io.File;
import java.io.IOException;

public class TreeDeepMain {
    public static void main(String[] args) {
        // Нужно ровно 2 аргумента: входной файл и выходной
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
        // Создадим папку, в которую будем сохранять промежуточные результаты
        File dirTmp = new File(System.getProperty("user.dir"), "tmp");
        dirTmp.mkdir();

        TreeDeep tree = new TreeDeep(fin, fout, dirTmp);
        try {
            tree.go();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            // Удаляем за собой
            for (File f : dirTmp.listFiles()) {
                f.delete();
            }
            dirTmp.delete();
        }
    }
}
