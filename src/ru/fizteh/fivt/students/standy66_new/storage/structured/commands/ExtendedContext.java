package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.Context;
import ru.fizteh.fivt.students.standy66_new.storage.structured.StructuredDatabase;
import ru.fizteh.fivt.students.standy66_new.storage.structured.table.StructuredTable;

/**
 * Created by andrew on 14.11.14.
 */
public class ExtendedContext extends Context {
    private StructuredDatabase structuredDatabase;
    private StructuredTable currentStructuredTable;

    public ExtendedContext(StructuredDatabase database) {
        super(database.getBackendDatabase());
        structuredDatabase = database;
    }

    public StructuredDatabase getStructuredDatabase() {
        return structuredDatabase;
    }

    public StructuredTable getCurrentStructuredTable() {
        return currentStructuredTable;
    }

    public void setCurrentStructuredTable(StructuredTable currentStructuredTable) {
        this.currentStructuredTable = currentStructuredTable;
        setCurrentTable(currentStructuredTable.getBackendTable());
    }
}
