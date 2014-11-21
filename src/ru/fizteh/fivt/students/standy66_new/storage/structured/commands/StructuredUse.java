package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;
import ru.fizteh.fivt.students.standy66_new.exceptions.UncommitedChangesException;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

import java.io.PrintWriter;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredUse extends ExtendedContextualCommand {

    public static final boolean WARN_OF_UNSAVED_CHANGES = System.getProperty("warn_unsaved") != null;

    protected StructuredUse(PrintWriter writer, ExtendedContext context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        StructuredTable newTable = getContext()
                .getStructuredDatabase().getTable(arguments[1]);
        StructuredTable currentTable = getContext().getCurrentStructuredTable();

        if (newTable == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            if (WARN_OF_UNSAVED_CHANGES) {
                if (currentTable != null && currentTable.getBackendTable().unsavedChangesCount() > 0) {
                    throw new UncommitedChangesException(String.format("%d unsaved changes",
                            currentTable.getBackendTable().unsavedChangesCount()));
                }
            }
            getContext().setCurrentStructuredTable(newTable);
            getOutputWriter().printf("using %s\n", arguments[1]);
        }
    }
}
