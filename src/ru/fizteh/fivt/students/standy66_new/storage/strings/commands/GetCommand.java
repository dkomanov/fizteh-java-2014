package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.exceptions.RowNotFoundException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class GetCommand extends ContextualCommand {
    protected GetCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        String value = current.get(arguments[1]);
        if (value == null) {
            throw new RowNotFoundException();
        } else {
            getOutputWriter().println("found");
            getOutputWriter().println(value);
        }
    }
}
