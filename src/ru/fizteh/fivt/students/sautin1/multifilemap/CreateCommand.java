package ru.fizteh.fivt.students.sautin1.multifilemap;

import ru.fizteh.fivt.students.sautin1.multifilemap.filemap.AbstractStringDatabaseCommand;
import ru.fizteh.fivt.students.sautin1.multifilemap.filemap.StringDatabaseState;
import ru.fizteh.fivt.students.sautin1.multifilemap.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.multifilemap.shell.UserInterruptException;

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
            state.getTableProvider().createTable(args[1]);
            System.out.println("created");
        } catch (IllegalArgumentException e) {
            System.err.println(args[1] + " exists");
        } catch (Exception e) {
            throw new CommandExecuteException(e.getMessage());
        }
    }
}
