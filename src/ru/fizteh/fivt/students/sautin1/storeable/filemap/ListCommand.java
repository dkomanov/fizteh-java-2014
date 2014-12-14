package ru.fizteh.fivt.students.sautin1.storeable.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.storeable.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.storeable.shell.UserInterruptException;

/**
 * List command.
 * Created by sautin1 on 10/12/14.
 */
public class ListCommand extends AbstractStoreableDatabaseCommand {

    public ListCommand() {
        super("list", 0, 0);
    }

    /**
     * List all the keys from the active table.
     * @param state - database state.
     * @param args - command arguments.
     * @throws UserInterruptException if user desires to exit.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(StoreableDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        GeneralTable<Storeable> table = state.getActiveTable();
        if (table != null) {
            System.out.println(String.join(", ", table.list()));
        } else {
            System.err.println("no table");
        }
    }
}
