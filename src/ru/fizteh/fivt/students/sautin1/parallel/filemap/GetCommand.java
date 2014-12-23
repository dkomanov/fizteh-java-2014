package ru.fizteh.fivt.students.sautin1.parallel.filemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.parallel.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTable;

/**
 * Get command.
 * Created by sautin1 on 10/12/14.
 */
public class GetCommand extends AbstractStoreableDatabaseCommand {

    public GetCommand() {
        super("get", 1, 1);
    }

    /**
     * Get value by key from the active table.
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
        StoreableTable table = state.getActiveTable();
        if (table != null) {
            Storeable value = table.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                String serialized = "";
                try {
                    serialized = state.getTableProvider().serialize(table, value);
                } catch (Exception e) {
                    System.err.println("wrong type " + e.getMessage());
                }
                System.out.println("found");
                System.out.println(serialized);
            }
        } else {
            System.err.println("no table");
        }
    }
}
