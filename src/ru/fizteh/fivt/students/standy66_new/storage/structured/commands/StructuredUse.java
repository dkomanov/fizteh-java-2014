package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;
import ru.fizteh.fivt.students.standy66_new.exceptions.UncommitedChangesException;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredUse extends ExtendedContextualCommand {

    public static final boolean WARN_OF_UNSAVED_CHANGES = System.getProperty("warn_unsaved") != null;

    protected StructuredUse(ExtendedContext context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        StructuredTable newTable = (StructuredTable) getExtendedContext()
                .getStructuredDatabase().getTable(arguments[1]);
        StructuredTable currentTable = getExtendedContext().getCurrentStructuredTable();

        if (newTable == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            if (WARN_OF_UNSAVED_CHANGES) {
                if (currentTable != null && currentTable.getBackendTable().unsavedChangesCount() > 0) {
                    throw new UncommitedChangesException(String.format("%d unsaved changes",
                            currentTable.getBackendTable().unsavedChangesCount()));
                }
            }
            getExtendedContext().setCurrentStructuredTable(newTable);
            System.out.printf("using %s\n", arguments[1]);
        }
    }
}
