package ru.fizteh.fivt.students.sautin1.storeable.filemap;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

/**
 * Tools for reading database tables from file and writing to file.
 * Created by sautin1 on 10/12/14.
 */
public abstract class TableIOTools<MappedValue, T extends GeneralTable<MappedValue>,
                              P extends GeneralTableProvider<MappedValue, T>> {
    /**
     * Read the whole table from the file.
     * @param rootPath - path to the root directory.
     * @param tableName - name of the source table.
     * @return filled table.
     * @throws java.io.IOException if any IO error occurs.
     */
    public abstract T readTable(P provider, Path rootPath, String tableName, Object[] args)
            throws IOException, ParseException;

    /**
     * Write the whole table to the file.
     * @param rootPath - path to the root directory.
     * @param table - table to write.
     */
    public abstract void writeTable(P provider, Path rootPath, T table) throws IOException;
}
