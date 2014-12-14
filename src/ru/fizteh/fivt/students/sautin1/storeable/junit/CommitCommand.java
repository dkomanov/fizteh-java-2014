package ru.fizteh.fivt.students.sautin1.storeable.junit;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.storeable.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.storeable.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.storeable.shell.UserInterruptException;

import java.io.IOException;

/**
 * Commit command.
 * Created by sautin1 on 10/12/14.
 */
public class CommitCommand extends AbstractStoreableDatabaseCommand {

    public CommitCommand() {
        super("commit", 0, 0);
    }

    /**
     * Commit all the changes made to the active table.
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
            try {
                int diffCount = state.getTableProvider().commitTable(table.getName());
                System.out.println(diffCount);
            } catch (IOException e) {
                throw new CommandExecuteException(e.getMessage());
            }
        } else {
            System.err.println("no table");
        }
    }
}
