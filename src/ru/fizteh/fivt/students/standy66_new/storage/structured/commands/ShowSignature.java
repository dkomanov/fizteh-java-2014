package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

import java.io.PrintWriter;

/**
 * @author andrew
 *         Created by andrew on 03.12.14.
 */
public class ShowSignature extends ExtendedContextualCommand {
    public ShowSignature(PrintWriter writer, ExtendedContext context) {
        super(writer, (x -> x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);

        StructuredDatabase db = getContext().getStructuredDatabase();
        getOutputWriter().println(String.format("%-20s%-20s%s", "table_name", "row_count", "signature"));
        for (String name : db.getTableNames()) {
            StructuredTable table = db.getTable(name);
            getOutputWriter().printf("%-20s%-20s%s%n", name, table.size(), table.getTableSignature().toString());
        }
    }
}
