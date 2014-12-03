package ru.fizteh.fivt.students.sautin1.multifilemap.filemap;

import ru.fizteh.fivt.students.sautin1.multifilemap.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.multifilemap.shell.UserInterruptException;

/**
 * Get command.
 * Created by sautin1 on 10/12/14.
 */
public class GetCommand extends AbstractStringDatabaseCommand {

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
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        GeneralTable<String> table = state.getActiveTable();
        if (table != null) {
            String value = table.get(args[1]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } else {
            System.err.println("no table");
        }
    }
}
