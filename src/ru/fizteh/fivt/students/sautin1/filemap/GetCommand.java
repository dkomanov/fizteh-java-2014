package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.shell.UserInterruptException;

/**
 * Created by sautin1 on 10/12/14.
 */
public class GetCommand extends AbstractStringDatabaseCommand {

    public GetCommand() {
        super("get", 1, 1);
    }

    @Override
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        String value = state.getActiveTable().get(args[1]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
