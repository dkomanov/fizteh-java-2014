package ru.fizteh.fivt.students.sautin1.junit;

import ru.fizteh.fivt.students.sautin1.junit.filemap.AbstractStringDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.junit.filemap.GeneralTable;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringDatabaseState;
import ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException;

/**
 * Size command.
 * Created by sautin1 on 10/12/14.
 */
public class SizeCommand extends AbstractStringDatabaseCommand {

    public SizeCommand() {
        super("size", 0, 0);
    }

    /**
     * Get the number of entries in the active table.
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
        GeneralTable<String> table = state.getActiveTable();
        if (table != null) {
            System.out.println(table.size());
        } else {
            System.err.println("no table");
        }
    }
}
