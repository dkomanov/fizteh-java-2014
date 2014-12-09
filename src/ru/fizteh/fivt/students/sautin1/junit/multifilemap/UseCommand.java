package ru.fizteh.fivt.students.sautin1.junit.multifilemap;

import ru.fizteh.fivt.students.sautin1.junit.filemap.*;
import ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException;

/**
 * Command for setting a definite database table as active.
 * Created by sautin1 on 10/20/14.
 */
public class UseCommand extends AbstractStringDatabaseCommand {

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
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        GeneralTableProvider<String, StringTable> provider = state.getTableProvider();
        GeneralTable<String> oldTable = state.getActiveTable();
        StringTable newTable = provider.getTable(args[1]);
        if (newTable != null) {
            int diffCount = 0;
            if (oldTable != null) {
                diffCount = oldTable.diffCount();
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
