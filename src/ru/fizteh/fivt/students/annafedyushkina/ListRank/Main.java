package ru.fizteh.fivt.students.annafedyushkina.ListRank;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Not enough arguments =^.^=");
            System.exit(1);
        }
        File file_in = new File(args[0]);
        if (file_in.exists() == false || file_in.isFile() == false) {
            System.err.println("Bad input =^.^=");
            System.exit(1);
        }
        File directory = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "tmp");
        directory.mkdir();
        File file_out = new File(args[1]);
        Ranking rank = new Ranking(file_in, file_out, directory);
        rank.do_some_magic();
        for (File c : directory.listFiles())
            c.delete();
        directory.delete();
    }
}
