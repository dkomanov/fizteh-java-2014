package ru.fizteh.fivt.students.andrewzhernov.shell;

import java.io.File;

public class Remove {
    public static void execute(String[] args) throws Exception {
        File file = null;
        if (args.length == 2) {
            file = ChangeDir.openFile(args[1]);
            if (file.isDirectory()) {
                throw new Exception("rm: " + args[1] + " is a directory");
            }
        } else if (args.length == 3 && args[1].equals("-r")) {
            file = ChangeDir.openFile(args[2]);
        } else {
            throw new Exception("Usage: rm [-r] <file|dir>");
        }

        if (!file.exists()) {
            throw new Exception("rm: " + file.getPath() + ": no such file or directory");
        }

        removeFile(file);
    }

    public static void removeFile(File file) throws Exception {
        boolean isRemoved = false;
        if (file.isFile()) {
            isRemoved = file.delete();
        } else {
            String[] list = file.list();
            for (String son : list) {
                removeFile(new File(file, son));
            }
            isRemoved = file.delete();
        }
        if (!isRemoved) {
            throw new Exception("rm: " + file.getCanonicalPath() + ": can't remove");
        }
    }
}
