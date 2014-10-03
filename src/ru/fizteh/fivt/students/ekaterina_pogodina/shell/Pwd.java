package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.IOException;
import java.nio.file.Paths;

public class Pwd {
    private Pwd() { }
    public static void run(String[] args, int j) throws IOException {
        if (j != 0) {
            System.err.println(args[0] + ": to many arguments");
        } else {
            try {
                String s =
                    Paths.get(CurrentDir.getCurrentDirectory()).toAbsolutePath().normalize().toString();
                System.out.print(s);
                System.out.print("\n");
            } catch (Exception e) {
                System.err.println(
                "mkdir: couldn't create the directory \'" + CurrentDir.getCurrentDirectory() + "\'.");
            }
        }
    }
}
