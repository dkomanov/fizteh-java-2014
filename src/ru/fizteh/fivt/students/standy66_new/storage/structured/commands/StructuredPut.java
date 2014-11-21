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
        super(writer, (x -> x == 3), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);

        StructuredTable current = getExtendedContext().getCurrentStructuredTable();
        StructuredDatabase database = getExtendedContext().getStructuredDatabase();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        Storeable currentValue = current.get(arguments[1]);
        Storeable newValue = database.deserialize(current, arguments[2]);
        if (currentValue == null) {
            out.println("new");
        } else {
            out.println("overwrite");
            out.println(database.serialize(current, currentValue));
        }
        current.put(arguments[1], newValue);
    }
}
