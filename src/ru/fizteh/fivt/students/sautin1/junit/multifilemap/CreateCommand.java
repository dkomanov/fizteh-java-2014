package ru.fizteh.fivt.students.sautin1.junit.multifilemap;

import ru.fizteh.fivt.students.sautin1.junit.filemap.AbstractStringDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringDatabaseState;
import ru.fizteh.fivt.students.sautin1.junit.filemap.StringTable;
import ru.fizteh.fivt.students.sautin1.junit.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException;

/**
 * Command for creating database table.
 * Created by sautin1 on 10/20/14.
 */
public class CreateCommand extends AbstractStringDatabaseCommand {

    public CreateCommand() {
        super("create", 1, 1);
    }

    /**
     * Create new table.
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
        try {
            StringTable newTable = state.getTableProvider().createTable(args[1]);
            if (newTable == null) {
                System.err.println(args[1] + " exists");
            } else {
                System.out.println("created");
            }
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
