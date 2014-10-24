package ru.fizteh.fivt.students.moskupols.multifilehashmap;

import java.io.IOException;

/**
 * Created by moskupols on 23.10.14.
 */
public class DataBaseCursor {
    private MultiFileMap currentTable;

    public DataBaseCursor() {
        currentTable = null;
    }

    public MultiFileMap getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(MultiFileMap currentTable) {
        if (currentTable != null)
            try {
                currentTable.flush();
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Couldn't save %s", currentTable.getName()));
            }
        this.currentTable = currentTable;
    }
}
