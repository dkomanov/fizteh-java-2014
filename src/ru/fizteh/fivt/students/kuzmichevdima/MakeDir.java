/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

import java.io.File;

public class MakeDir {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 6;
    private static final int CANT_CREATE_EXIT_CODE = 7;
    private static final int ALREADY_EXISTS_EXIT_CODE = 8;
    public MakeDir(final String [] args, final CurrentDir dir) {
        if (args.length != 2) {
            System.err.println("invalid number of arguments for mkdir");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        File f = new File(dir.getCurrentDir(), args[1]);
        if (f.exists()) {
            System.err.println("mkdir: this directory already exists");
            System.exit(ALREADY_EXISTS_EXIT_CODE);
        }
        if (!f.mkdirs()) {
            System.err.println("Can't create directory");
            System.exit(CANT_CREATE_EXIT_CODE);
        }
    }
}
