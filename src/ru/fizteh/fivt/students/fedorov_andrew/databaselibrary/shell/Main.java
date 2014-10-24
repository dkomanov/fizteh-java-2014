package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

/**
 * Created by phoenix on 24.10.14.
 */
public class Main {
    //java -Dfizteh.db.dir=/home/phoenix/test/DB ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.Main

    public static void main(String[] args) {
        Shell<SingleDatabaseShellState> shell =
                new Shell<SingleDatabaseShellState>(new SingleDatabaseShellState());
        if (args.length == 0) {
            shell.run(System.in);
        } else {
            shell.run(args);
        }
    }
}
