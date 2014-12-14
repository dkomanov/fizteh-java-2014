package ru.fizteh.fivt.students.sautin1.parallel.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;

/**
 * Remove command.
 * Created by sautin1 on 10/12/14.
 */
public class RemoveCommand extends AbstractStoreableDatabaseCommand {

    public RemoveCommand() {
        super("remove", 1, 1);
    }

    /**
     * Remove entry by its key from the active table.
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
            Storeable value = table.remove(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } else {
            System.err.println("no table");
        }
    }
}
