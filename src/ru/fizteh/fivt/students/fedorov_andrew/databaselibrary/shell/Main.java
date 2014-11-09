package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;

public class Main {
    //java -Dfizteh.db.dir=/home/phoenix/test/DB ru.fizteh.fivt.students.fedorov_andrew
    // .databaselibrary.shell.Main

    public static void main(String[] args) {
        try {
            Shell<SingleDatabaseShellState> shell = new Shell<>(new SingleDatabaseShellState());
            int exitCode;
            if (args.length == 0) {
                exitCode = shell.run(System.in);
            } else {
                exitCode = shell.run(args);
            }

            System.exit(exitCode);
        } catch (TerminalException exc) {
            // Already handled.
            System.exit(1);
        }
    }
}
