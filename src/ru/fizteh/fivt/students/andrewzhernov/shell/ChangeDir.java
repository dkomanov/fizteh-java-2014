package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;

public class ChangeDir {
    public static File openFile(String file) {
        if (file.charAt(0) == File.separatorChar) {
            return new File(file);
        } else {
            return new File(System.getProperty("user.dir") + File.separator + file);
        }
    }

    public static void execute(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: cd <absolute path|relative path>");
        } else {
            File file = openFile(args[1]);
            if (file.isDirectory()) {
                System.setProperty("user.dir", file.getCanonicalPath());
            } else if (file.exists()) {
                throw new Exception("cd: " + args[1] + ": isn't a directory");
            } else {
                throw new Exception("cd: " + args[1] + ": no such file or directory");
            }
        }
    }
}
