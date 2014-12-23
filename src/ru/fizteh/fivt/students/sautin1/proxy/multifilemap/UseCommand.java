package ru.fizteh.fivt.students.sautin1.proxy.multifilemap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.sautin1.proxy.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.proxy.storeable.StoreableTable;
import ru.fizteh.fivt.students.sautin1.proxy.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.proxy.filemap.GeneralTableProvider;
import ru.fizteh.fivt.students.sautin1.proxy.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.proxy.storeable.AbstractStoreableDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.proxy.storeable.StoreableDatabaseState;

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
                System.out.println("using " + args[1]);
            } else {
                System.err.println(diffCount + " unsaved changes");
            }
        } else {
            System.err.println(args[1] + " not exists");
        }
    }
}
