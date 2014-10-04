package ru.fizteh.fivt.students.sautin1.shell;

import static ru.fizteh.fivt.students.sautin1.shell.CommandParser.convertArrayToString;

/**
 * Represents Unix shell with interactive and non-interactive modes available.
 * Uses class Shell.
 * Created by sautin1 on 9/30/14.
 */
public class ShellMain {

    public static void main(String[] args) {
        Shell shell = new Shell();
        if (args.length == 0) {
            // interactive mode
            shell.interactWithUser();
        } else {
            // non-interactive mode
            int exitCode = 0;
            try {
                shell.callCommands(convertArrayToString(args));
            } catch (Exception e) {
                System.err.println(e.getMessage());
                exitCode = 1;
            }
            System.exit(exitCode);
        }
    }

}
