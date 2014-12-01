package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;

public class MakeDir {
    public static void execute(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: mkdir <dirname>");
        } else {
            File dir = ChangeDir.openFile(args[1]);
            if (!dir.mkdir()) {
                throw new Exception("mkdir: " + args[1] + ": can't create directory");
            }
        }
    }
}
