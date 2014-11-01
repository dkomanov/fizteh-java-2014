package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.exceptions.RowNotFoundException;

/**
 * Created by astepanov on 20.10.14.
 */
public class GetCommand extends ContextualCommand {
    protected GetCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException("no table");
        }
        String value = current.get(arguments[1]);
        if (value == null) {
            throw new RowNotFoundException("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
