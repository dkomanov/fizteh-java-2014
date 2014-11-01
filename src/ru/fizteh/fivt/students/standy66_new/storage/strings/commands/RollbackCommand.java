package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

/**
 * Created by andrew on 01.11.14.
 */
public class RollbackCommand extends ContextualCommand {
    public RollbackCommand(Context context) {
        super((x -> x == 1), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);

        Table currentTable = getContext().getCurrentTable();
        if (currentTable == null) {
            throw new NoTableSelectedException("no table");
        }
        currentTable.rollback();
    }
}
