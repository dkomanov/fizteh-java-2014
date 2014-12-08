package ru.fizteh.fivt.students.sautin1.junit.filemap;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Tools for reading database tables from file and writing to file.
 * Created by sautin1 on 10/12/14.
 */
public interface TableIOTools<MappedValue, T extends GeneralTable<MappedValue>> {
    /**
     * Read the whole table from the file.
     * @param rootPath - path to the root directory.
     * @param provider - string table provider.
     * @param tableName - name of the source table.
     * @return filled table.
     * @throws java.io.IOException if any IO error occurs.
     */
    T readTable(Path rootPath, GeneralTableProvider<MappedValue, T> provider, String tableName) throws IOException;

    /**
     * Write the whole table to the file.
     * @param rootPath - path to the root directory.
     * @param table - table to write.
     */
    void writeTable(Path rootPath, T table) throws IOException;
}
