package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredPut extends ExtendedContextualCommand {
    protected StructuredPut(ExtendedContext context) {
        super((x -> x == 3), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);

        StructuredTable current = getExtendedContext().getCurrentStructuredTable();
        StructuredDatabase database = getExtendedContext().getStructuredDatabase();
        if (current == null) {
            throw new NoTableSelectedException("no table");
        }
        Storeable currentValue = current.get(arguments[1]);
        Storeable newValue = database.deserialize(current, arguments[2]);
        if (currentValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(database.serialize(current, currentValue));
        }
        current.put(arguments[1], newValue);
    }
}
