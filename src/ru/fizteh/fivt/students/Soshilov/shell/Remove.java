package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 21:06
 */
public class Remove {
    /**
     * Remove recursive files and directories
     * @param f File, which is now under overview
     */
    private static void deleteDirectoriesAndFiles(final File f) {
        File[] arrayOfFiles = null;
        if (f.isDirectory()) {
            try {
                arrayOfFiles = f.listFiles();
            } catch (NullPointerException e2) {
                System.err.println("Error: cannot list files");
                System.exit(1);
            }
            if (arrayOfFiles != null) {
                for (File tmp : arrayOfFiles) {
                    deleteDirectoriesAndFiles(tmp);
                }
            }
            if (!f.delete()) {
                System.err.println("Error: deleting did not work");
                System.exit(1);
            }
        }
        if (!f.delete()) {
            System.err.println("Error: deleting did not work");
            System.exit(1);
        }
        if (f.exists()) {
            System.err.println("Error: cannot remove " + f.toString());
        }
    }

    /**
     * We remove the files and directories from arguments like UNIX rm
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param currentDirectory Current directory where we are now
     */
    public static void removeRun(final String[] currentArgs, final CurrentDirectory currentDirectory) {
//        if (currentArgs.length > 3) {
//            System.err.println("extra arguments are detected");
//            System.exit(1);
//        }
        if (currentArgs.length == 1) {
            System.err.println("rm: missing operand");
            System.exit(1);
        } else if (currentArgs.length == 2) {
            File f = new File(currentDirectory.getCurrentDirectory(), currentArgs[1]);
            if (!f.exists()) {
                System.err.println("rm: cannot remove ‘" + f.toString() + "’: No such file or directory");
                System.exit(1);
            }
            if (!f.delete()) {
                System.err.println("Error: cannot delete");
                System.exit(5);
                }
        } else if (currentArgs[1].equals("-r")) {
            File f = new File(currentDirectory.getCurrentDirectory(), currentArgs[2]);
            if (!f.exists()) {
                System.err.println("rm: cannot remove ‘" + f.toString() + "’: No such file or directory");
                System.exit(1);
            }
            deleteDirectoriesAndFiles(f);
        } else {
            System.err.println("Error: input data incorrect");
            System.exit(2);
        }

    }
}
        /*
        boolean isRmPresented = false;
        for (int i = 1; i < currentArgs.length; ++i) {
            if (currentArgs[i].equals("-r")) {
                isRmPresented = true;
                break;
            }
        }
        for (int i = 1; i < currentArgs.length; ++i) {
            if (currentArgs[i].equals("-r")) {
                continue;
            }
            File f = new File(currentDirectory.getCurrentDirectory(), currentArgs[i]);
            if (!f.exists()) {
                System.err.println("rm: cannot remove ‘" + f.toString() + "’: No such file or directory");
                continue;
            }
            if (isRmPresented) {
                deleteDirectoriesAndFiles(f);
            } else {
                if (f.isDirectory()) {
                    System.err.println("rm: cannot remove ‘" + f.toString() + "’: Is a directory");
                    continue;
                }
                if (!f.delete()) {
                    System.err.println("Error: deleting did not work");
                    System.exit(3);
                }
            }
        }*/
