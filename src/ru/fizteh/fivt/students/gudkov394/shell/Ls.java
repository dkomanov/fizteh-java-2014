package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

//работает
public class Ls {
    public Ls(String[] currentArgs, CurrentDirectory cd) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for ls");
            System.exit(1);
        }
        File f = new File(cd.getCurrentDirectory());
        File[] s = null;
        try {
            s = f.listFiles();
        } catch (NullPointerException e2) {
            System.err.println("problem with listFiles in Ls");
            System.exit(2);
        }
        if (s != null) {
            for (File tmp : s) {
                System.out.println(tmp.getName());
            }
        }
    }
}
