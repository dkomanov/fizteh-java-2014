package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.exceptions.RowNotFoundException;

/**
 * Created by astepanov on 20.10.14.
 */
public class RemoveCommand extends ContextualCommand {
    protected RemoveCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException("no table");
        }
        if (current.get(arguments[1]) == null) {
            throw new RowNotFoundException("not found");
        }
        System.out.println("removed");
        current.remove(arguments[1]);

    }
}
