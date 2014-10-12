package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.shell.CommandExecuteException;
import ru.fizteh.fivt.students.sautin1.shell.UserInterruptException;

/**
 * Created by sautin1 on 10/12/14.
 */
public class RemoveCommand extends AbstractStringDatabaseCommand {

    public RemoveCommand() {
        super("remove", 1, 1);
    }

    @Override
    public void execute(StringDatabaseState state, String... args)
            throws UserInterruptException, CommandExecuteException {

    }
}
