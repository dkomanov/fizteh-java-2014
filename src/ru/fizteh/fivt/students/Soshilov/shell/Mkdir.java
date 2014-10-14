package ru.fizteh.fivt.students.Soshilov.shell;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 02 October 2014
 * Time: 20:54
 */
public class Mkdir {
    /**
     * We make new directory as UNIX mkdir
     * @param currentArgs Arguments, entered with command (operators, files)
     * @param currentDirectory Current directory where we are now
     */
    public static void mkdirRun(final String[] currentArgs, final CurrentDirectory currentDirectory) {
        if (currentArgs.length == 1) {
            System.err.println("mkdir: missing operand");
            System.exit(1);
        }
        for (int i = 1; i < currentArgs.length; ++i) {
            File f = new File(currentDirectory.getCurrentDirectory(), currentArgs[i]);
            if (f.exists()) {
                System.err.println("mkdir: cannot create directory ‘" + f.toString() + "’: File exists");
                continue;
            }
            if (!f.mkdirs()) {
                //Creating the directory named by this abstract pathname
                System.err.println("Error: cannot create the directory");
                System.exit(1);
            }
        }
    }
}
