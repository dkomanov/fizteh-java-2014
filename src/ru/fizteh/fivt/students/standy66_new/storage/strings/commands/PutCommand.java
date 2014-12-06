package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class PutCommand extends ContextualCommand {

    protected PutCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 3), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        String currentValue = current.get(arguments[1]);
        if (currentValue == null) {
            getOutputWriter().println("new");
        } else {
            getOutputWriter().println("overwrite");
            getOutputWriter().println(currentValue);
        }
        current.put(arguments[1], arguments[2]);

    }
}
