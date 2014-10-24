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

    public void setCurrentTable(MultiFileMap currentTable) throws IOException {
        if (currentTable != null) {
            try {
                currentTable.flush();
            } catch (IOException e) {
                throw new IOException(
                        String.format("Couldn't save %s: %s", currentTable.getName(), e.getMessage()));
            }
        }
        this.currentTable = currentTable;
    }
}
