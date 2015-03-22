package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.shell.StringParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Rm {
    public static void run(String[] args) throws Exception {
        int afterKeyIndex = 1;
        if (args.length > 2 && args[1].equals("-r")) {
            afterKeyIndex = 2;
        }
        if (args.length == 1 || (args.length == 2 && args[1].equals("-r"))) {
            throw new Exception("rm : missing operand");
        } else if (args.length > 3 || (args.length > 2 && !args[1].equals("-r"))) {
            throw new Exception("rm : too much arguments");
        }
        try {
            File file = Paths.get(args[afterKeyIndex]).normalize().toFile();
            if (!file.isAbsolute()) {
                file = Paths
                        .get(System.getProperty("user.dir"),
                                args[afterKeyIndex]).normalize().toFile();
            }
            if (args[1].isEmpty() || !file.exists()) {
                throw new Exception(
                        "rm : cannot remove : No such file or directory");
            }
            if (file.isFile()) {
                if (!file.delete()) {
                    throw new Exception("rm : unexpectable error");
                }
            } else {
                if (afterKeyIndex == 1) {
                    throw new Exception("rm : cannot remove" + args[1]
                            + " is a directory");
                }
                if (!rmRec(file)) {
                    throw new Exception("rm : unexpectable error");
                }
            }

        } catch (InvalidPathException e) {
            throw new Exception("rm : cannot remove file : invelid character");
        } catch (SecurityException e) {
            throw new Exception("rm : cannot remove file : access denied");
        }
    }

    public static boolean rmRec(File file) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (!rmRec(f)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

}