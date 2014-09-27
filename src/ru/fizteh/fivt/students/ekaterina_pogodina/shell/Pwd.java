package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.nio.file.Paths;

public class Pwd {
    private Pwd() {
    }

    public static void run() {
        String s = Paths.get(CurrentDir.getCurrentDirectory()).toAbsolutePath().normalize().toString();
        System.out.print(s);
        System.out.print("\n");
    }
}
