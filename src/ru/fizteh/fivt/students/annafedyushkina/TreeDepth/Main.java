package ru.fizteh.fivt.students.annafedyushkina.TreeDepth;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Not enough arguments =^.^=");
            System.exit(1);
        }
        File fileIn = new File(args[0]);
        if (!(fileIn.exists()) || !(fileIn.isFile())) {
            System.err.println("Bad input =^.^=");
            System.exit(1);
        }
        File directory = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "tmp");
        directory.mkdir();
        File fileOut = new File(args[1]);
        FindingDepth depth = new FindingDepth(fileIn, fileOut, directory);
        depth.doSomeMagic();
        for (File c : directory.listFiles()) {
            c.delete();
        }
        directory.delete();
    }
}
