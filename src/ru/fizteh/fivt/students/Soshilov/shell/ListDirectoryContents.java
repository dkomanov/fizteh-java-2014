package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 20:45
 */
public class ListDirectoryContents {
    /**
     * List content of this directory like UNIX ls
     * @param currentDirectory Current directory where we are now
     */
    public static void listDirectoryContentsRun(final CurrentDirectory currentDirectory) {
//        Making just a ls without options and files
//        if (currentArgs.length > 1) {
//            System.err.println("extra arguments are detected");
//            System.exit(1);
//        }
        File f = new File(currentDirectory.getCurrentDirectory());
        File[] s = null;
        try {
            s = f.listFiles();
            // Returns an array of abstract pathnames denoting the files and directories
            // in the directory denoted by this abstract pathname that satisfy the specified filter
        } catch (NullPointerException e2) {
            System.err.println("Error: cannot list files");
            System.exit(1);
        }
        if (s != null) {
            for (File tmp : s) {
                System.out.println(tmp.getName());
                //Returns the name of the file or directory denoted by this abstract pathname
            }
        }
    }
}
