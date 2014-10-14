/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

public class Pwd {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 8;
    public Pwd(final String [] args, CurrentDir dir) {
        if (args.length != 1) {
            System.err.println("invalid number of arguments for pwd");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        System.out.println(dir.getCurrentDir());
    }
}
