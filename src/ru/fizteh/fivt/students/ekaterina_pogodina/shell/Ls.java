package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.File;

public final class Ls {
    private Ls() {
        //
    }

    public static void run() {
        File file = new File(CurrentDir.getCurrentDirectory());
        String[] s = file.list();
        for (int i = 0; i < s.length; i++) {
            System.out.print(s[i] + " ");
        }
        System.out.print("\n");
    }
}
