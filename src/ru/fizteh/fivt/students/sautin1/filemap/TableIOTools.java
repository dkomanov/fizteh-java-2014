package ru.fizteh.fivt.students.sautin1.filemap;

import java.nio.file.Path;

/**
 * Created by sautin1 on 10/12/14.
 */
public class TableIOTools<MappedValue, T extends GeneralTable<MappedValue>> {
    protected int dirQuantity;
    protected int fileQuantity;

    public TableIOTools(int dirQuantity, int fileQuantity) {
        this.dirQuantity = dirQuantity;
        this.fileQuantity = fileQuantity;
    }

    public T readTable(Path rootPath, String tableName) {
        return null;
    }

    public void writeTable(Path rootPath, T table) {

    }
}
