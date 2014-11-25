package ru.fizteh.fivt.students.moskupols.multifilehashmap;

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
        this.currentTable = currentTable;
    }
}
