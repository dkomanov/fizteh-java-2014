package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 18:21
 */
public class ChangeDirectory {
    /**
     * We change directory like UNIX cd
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param currentDirectory Current directory where we are now
     */
    public static void changeDirectoryRun(final String[] currentArgs, final CurrentDirectory currentDirectory) {
//        Because he go only to the first directory
//        if (currentArgs.length > 2) {
//            System.err.println("extra arguments are detected");
//            System.exit(1);
//        }
        if (currentArgs.length == 1) {
            currentDirectory.changeCurrentDirectory(currentDirectory.getHome());
        } else {
            if (currentArgs[1].equals("~")) {
                currentDirectory.changeCurrentDirectory(currentDirectory.getHome());
            }
            if (currentArgs[1].equals("..")) {
                File f = new File(currentDirectory.getCurrentDirectory());
                currentDirectory.changeCurrentDirectory(f.getParent());
            } else
            if (!currentArgs[1].equals(".")) {
                File f = new File(currentArgs[1]);
                if (!f.isAbsolute()) {
                    // Tests whether this abstract pathname is absolute.
                    f = new File(currentDirectory.getCurrentDirectory(), currentArgs[1]);
                    if (!f.exists()) {
                        System.err.println(f.toString() + ": No such file or directory");
                        System.exit(1);
                    }
                    if (!f.isFile()) {
                        System.err.println(f.toString() + ": Not a directory");
                        System.exit(1);
                    }
                }
                try {
                    currentDirectory.changeCurrentDirectory(f.getCanonicalPath());
                    //Returns the canonical pathname string of this abstract pathname.
                } catch (IOException e) {
                    System.err.println("Error: cannot find the pathname of a directory");
                    System.exit(1);
                }
            }
        }
    }
}
