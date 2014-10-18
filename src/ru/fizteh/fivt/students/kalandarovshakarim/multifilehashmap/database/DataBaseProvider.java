/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author shakarim
 */
public class DataBaseProvider implements TableProvider {

    private Map<String, Table> tables = null;
    private final Path directory;

    public DataBaseProvider(String directory) throws IOException {
        this.tables = new HashMap<>();
        this.directory = Paths.get(directory);
        load();
    }

    @Override
    public Table getTable(String name) {
        checkTableName(name);
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        checkTableName(name);

        if (tables.containsKey(name)) {
            return null;
        }

        Table newTable = null;

        try {
            newTable = new MultiFileTable(directory.toString(), name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        tables.put(name, newTable);

        return newTable;
    }

    @Override
    public void removeTable(String name) {
        checkTableName(name);

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not found");
        } else {
            ShellUtils utils = new ShellUtils();
            Path tablePath = directory.resolve(name);
            try {
                utils.rm(tablePath.toString(), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            tables.remove(name);
        }
    }

    public List<String> listTables() {
        String[] array = tables.keySet().toArray(new String[0]);
        return Arrays.asList(array);
    }

    private void load() throws IOException {
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory);
        for (Path pathToTable : directoryStream) {
            String tableName = pathToTable.getFileName().toString();
            if (Files.isDirectory(pathToTable)) {
                Table table = new MultiFileTable(directory.toString(), tableName);
                tables.put(tableName, table);
            } else {
                String format = "'%s' contains non-directory files";
                String eMessage = String.format(format, directory);
                throw new IOException(eMessage);
            }
        }
    }

    void checkTableName(String tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("Null cannot be an argument");
        }
    }
}
