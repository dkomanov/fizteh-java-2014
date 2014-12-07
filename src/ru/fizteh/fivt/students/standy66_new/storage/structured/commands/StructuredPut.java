package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

import java.io.PrintWriter;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredPut extends ExtendedContextualCommand {
    protected StructuredPut(PrintWriter writer, ExtendedContext context) {
        super(writer, (x -> x >= 3), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        StringBuilder mergedArguments = new StringBuilder();
        for (int i = 2; i < arguments.length; i++) {
            mergedArguments.append(arguments[i]);
            if (i != arguments.length - 1) {
                mergedArguments.append(' ');
            }
        }

        StructuredTable current = getContext().getCurrentStructuredTable();
        StructuredDatabase database = getContext().getStructuredDatabase();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        Storeable currentValue = current.get(arguments[1]);
        Storeable newValue = database.deserialize(current, mergedArguments.toString());
        if (currentValue == null) {
            getOutputWriter().println("new");
        } else {
            getOutputWriter().println("overwrite");
            getOutputWriter().println(database.serialize(current, currentValue));
        }
        current.put(arguments[1], newValue);
    }
}
