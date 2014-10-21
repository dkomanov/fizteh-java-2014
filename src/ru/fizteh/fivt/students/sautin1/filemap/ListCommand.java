package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.filemap.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.filemap.shell.UserInterruptException;

/**
 * List command.
 * Created by sautin1 on 10/12/14.
 */
public class ListCommand extends AbstractStringDatabaseCommand {

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
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        System.out.println(String.join(", ", state.getActiveTable().list()));
        /*for (String key : state.getActiveTable().list()) {
            System.out.print(key);
        }*/
    }
}
