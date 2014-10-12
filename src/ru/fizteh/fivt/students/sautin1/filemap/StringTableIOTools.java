package ru.fizteh.fivt.students.sautin1.filemap;

import java.nio.file.Path;

/**
 * Created by sautin1 on 10/12/14.
 */
public class StringTableIOTools extends TableIOTools<String, StringTable> {

    public StringTableIOTools(int dirQuantity, int fileQuantity) {
        super(dirQuantity, fileQuantity);
    }

    public StringTableIOTools() {
        super(1, 1);
    }

    @Override
    public StringTable readTable(Path rootPath, String tableName) {
        return null;
    }

    @Override
    public void writeTable(Path rootPath, StringTable table) {

    }


}
