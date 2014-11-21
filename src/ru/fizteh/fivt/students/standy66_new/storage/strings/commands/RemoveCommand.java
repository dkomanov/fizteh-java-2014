package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.exceptions.RowNotFoundException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class RemoveCommand extends ContextualCommand {
    protected RemoveCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        if (current.get(arguments[1]) == null) {
            throw new RowNotFoundException();
        }
        out.println("removed");
        current.remove(arguments[1]);

    }
}
