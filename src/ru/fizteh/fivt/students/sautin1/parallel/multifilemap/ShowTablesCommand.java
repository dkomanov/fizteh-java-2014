package ru.fizteh.fivt.students.sautin1.parallel.multifilemap;

import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTable;

/**
 * Command for showing all tables in database.
 * Created by sautin1 on 10/20/14.
 */
public class ShowTablesCommand extends AbstractStoreableDatabaseCommand {

    public ShowTablesCommand() {
        super("show", 1, 1);
    }

    /**
     * Lists names and sizes of all tables in the database.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StoreableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + " tables: wrong number of arguments");
        }
        if (!args[1].equals("tables")) {
            throw new CommandExecuteException(toString() + " " + args[1] + ": command not found");
        }
        for (StoreableTable table: state.getTableProvider().listTables()) {
            System.out.println(table.getName() + " " + table.size());
        }
    }
}
