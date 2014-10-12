package ru.fizteh.fivt.students.sautin1.filemap;

import java.nio.file.Path;

/**
 * Created by sautin1 on 10/12/14.
 */
public class StringTableIOTools implements TableIOTools<String, StringTable> {
    private int dirQuantity;
    private int fileQuantity;

    public StringTableIOTools(int dirQuantity, int fileQuantity) {
        this.dirQuantity = dirQuantity;
        this.fileQuantity = fileQuantity;
    }

    public StringTableIOTools() {
        this(0, 1);
    }

    @Override
    public StringTable readTable(Path rootPath, String tableName) {
        return null;
    }

    @Override
    public void writeTable(Path rootPath, StringTable table) {

    }


}
