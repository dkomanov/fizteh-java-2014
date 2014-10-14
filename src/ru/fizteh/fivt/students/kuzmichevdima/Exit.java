/**
 * Created by kuzmi_000 on 14.10.14.
 */

package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

public class Exit {
    private static final int INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE = 19;
    public Exit(final String [] args) {
        if (args.length > 1) {
            System.err.println("invalid number of arguments for exit");
            System.exit(INVALID_NUMBER_OF_ARGUMENTS_EXIT_CODE);
        }
        System.exit(0);
    }
}
