package ru.fizteh.fivt.students.sautin1.junit.filemap;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by sautin1 on 10/28/14.
 */
public abstract class StringTableIOTools implements TableIOTools<String, StringTable> {
    @Override
    public abstract StringTable readTable(Path rootPath, GeneralTableProvider<String, StringTable> provider,
                                          String tableName) throws IOException;

    @Override
    public abstract void writeTable(Path rootPath, StringTable table) throws IOException;
}
