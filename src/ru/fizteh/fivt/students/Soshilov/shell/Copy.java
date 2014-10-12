package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 03 October 2014
 * Time: 17:29
 */
public class Copy {
    /**
     * Recursive copy.
     *
     * @param from The object
     * @param to   The destination
     */
    private static void copyRecursive(final File from, final File to) {
        try {
            CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
            Files.copy(from.toPath(), to.toPath(), options);
        } catch (IOException e2) {
            System.err.println("Error: cannot copy file");
            System.exit(1);
        }
        File[] arrayOfFile = null;
        if (from.isDirectory()) {
            try {
                arrayOfFile = from.listFiles();
            } catch (NullPointerException e1) {
                System.err.println("Error: cannot list file");
                System.exit(1);
            }
            if (arrayOfFile != null) {
                for (File f : arrayOfFile) {
                    File newTo = new File(to.getAbsolutePath(), f.getName());
                    copyRecursive(f, newTo);
                }
            }
        }
    }

    /**
     * Copy files and directories like UNIX cp, but only copy
     *
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param cd          Current directory where we are now
     */
    public static void copyRun(final String[] currentArgs, final CurrentDirectory cd) {
        if ((currentArgs.length > 4) || (currentArgs.length == 4) && (!currentArgs[1].equals("-r"))) {
//            We have extra arguments: more than 2 files or directories
            System.out.println("extra arguments are detected");
            System.exit(1);
        }
        /**
         * upping point is 0 if coping is not recursive and 1 in other way
         */
        int uppingPoint = 0;
        if ((currentArgs.length == 4) && (!currentArgs[1].equals("-r"))) {
            uppingPoint = 1;
        }
        File from = new File(currentArgs[1 + uppingPoint]);
        File to = new File(currentArgs[2 + uppingPoint]);
        if (!from.isAbsolute()) {
            from = new File(cd.getCurrentDirectory(), currentArgs[1 + uppingPoint]);
        }
        if (!to.isAbsolute()) {
            to = new File(cd.getCurrentDirectory(), currentArgs[2 + uppingPoint]);
        }

        if ((uppingPoint == 0) && (from.isDirectory())) {
//          when first is a directory we can't copy without "-r"
            System.err.println("cp: omitting directory ‘" + currentArgs[1] + "’");
            System.exit(1);
        }
        if ((uppingPoint == 1) && (from.isDirectory() && to.isDirectory())) {
//          Both arguments are directories
            File newTo = new File(to.getAbsolutePath(), from.getName());
            try {
                CopyOption[] options = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
                Files.copy(from.toPath(), newTo.toPath(), options);
            } catch (IOException e2) {
                System.err.println("Error: cannot copy");
                System.exit(3);
            }
            copyRecursive(from, newTo);
        } else if (from.isFile() && to.isDirectory()) {
            try {
                File newTo = new File(to.getAbsolutePath(), from.getName());
                Files.copy(from.toPath(), newTo.toPath());
            } catch (IOException e2) {
                System.err.println("Error: cannot copy");
                System.exit(1);
            }
        } else {
            System.err.println("Error: cannot copy");
            System.exit(1);
        }
    }
}
