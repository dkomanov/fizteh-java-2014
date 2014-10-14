/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;
import java.io.IOException;

public class ChangeDir {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 3;
    private static final int NO_DIRECTORY_EXIT_CODE = 4;
    private static final int CD_EXCEPTION_EXIT_CODE = 5;

    public ChangeDir(final String[] args, final CurrentDir dir) {
        if (args.length == 1) {
            dir.changeCurrentDir(dir.getHome());
            return;
        }
        if (args.length != 2) {
            System.err.println("invalid number of arguments for cd");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        if (args[1].equals("..")) {
            File f = new File(dir.getCurrentDir());
            dir.changeCurrentDir(f.getParent());
            return;
        }
        if (args[1].equals(".")) {
            return;
        }
        File f = new File(args[1]);
        if (!f.isAbsolute()) {
            f = new File(dir.getCurrentDir(), args[1]);
        }
        if (!f.exists()) {
            System.err.println("there is no such directory: " + f.getPath());
            System.exit(NO_DIRECTORY_EXIT_CODE);
        }
        try {
            dir.changeCurrentDir(f.getCanonicalPath());
        } catch (IOException exception) {
            System.err.println("can't change directory");
            System.exit(CD_EXCEPTION_EXIT_CODE);
        }
    }
}
