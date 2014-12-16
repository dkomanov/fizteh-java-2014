package ru.fizteh.fivt.students.sautin1.telnet.multifilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.telnet.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTable;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GeneralTableProvider;
import ru.fizteh.fivt.students.sautin1.telnet.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;

/**
 * Command for setting a definite database table as active.
 * Created by sautin1 on 10/20/14.
 */
public class UseCommand extends AbstractStoreableDatabaseCommand {

    public UseCommand() {
        super("use", 1, 1);
    }

    /**
     * Set active table table.
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
        GeneralTableProvider<Storeable, StoreableTable> provider = state.getTableProvider();
        GeneralTable<Storeable> oldTable = state.getActiveTable();
        StoreableTable newTable = provider.getTable(args[1]);
        if (newTable != null) {
            int diffCount = 0;
            if (oldTable != null) {
                diffCount = oldTable.getNumberOfUncommittedChanges();
            }
            if (diffCount == 0) {
                state.setActiveTable(newTable);
                state.getOutStream().println("using " + args[1]);
            } else {
                throw new CommandExecuteException(diffCount + " unsaved changes");
            }
        } else {
            throw new CommandExecuteException(args[1] + " not exists");
        }
    }
}
