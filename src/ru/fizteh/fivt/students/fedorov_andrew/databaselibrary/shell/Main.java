package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ExitRequest;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TerminalException;

/**
 * Created by phoenix on 24.10.14.
 */
public class Main {
    //java -Dfizteh.db.dir=/home/phoenix/test/DB ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.Main

    public static void main(String[] args) {
        try {
            Shell<SingleDatabaseShellState> shell =
                    new Shell<SingleDatabaseShellState>(new SingleDatabaseShellState());
            if (args.length == 0) {
                shell.run(System.in);
            } else {
                shell.run(args);
            }
        } catch (ExitRequest request) {
            System.exit(request.getCode());
        } catch (TerminalException exc) {
            // Already handled.
            System.exit(1);
        }
    }
}
