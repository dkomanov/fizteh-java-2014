package ru.fizteh.fivt.students.RadimZulkarneev.DataBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

public class TableProviderRealize implements TableProvider {

    private Map<String, TableRealize> tables;
    private Path dataBasePath;

    public TableProviderRealize(String dataBasePathe) {
        this.dataBasePath = Paths.get(dataBasePathe);
        if (!dataBasePath.toFile().isDirectory() && dataBasePath.toFile().exists()) {
            throw new IllegalStateException(dataBasePathe + " is not a directory.");
        }
        if (!dataBasePath.toFile().exists()) {
            try {
                Files.createDirectory(dataBasePath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        tables = new HashMap<>();
        for (String currentTable : dataBasePath.toFile().list()) {
            try {
                TableRealize currentTableRealize = new TableRealize(dataBasePath.resolve(currentTable));
                tables.put(currentTable, currentTableRealize);
            } catch (Exception ex) {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public Table getTable(String name) {
        // TODO Auto-generated method stub
        if (!tables.containsKey(name)) {
            return null;
        }
        nameIsNullAssertion(name);
        return tables.get(name);
    }

    @Override
    public Table createTable(String name) {
        // TODO Auto-generated method stub
        if (tables.containsKey(name)) {
            return null;
        }
        nameIsNullAssertion(name);
        try {
            TableRealize createdTable = new TableRealize(dataBasePath.resolve(name));
            tables.put(name, createdTable);
            return createdTable;
        } catch (Exception e) {
            throw new IllegalStateException("table-create problem");
        }
    }

    @Override
    public void removeTable(String name) {
        // TODO Auto-generated method stub
        nameIsNullAssertion(name);
        if (tables.containsKey(name)) {
            try {
                tables.get(name).drop();
                tables.remove(name);
            } catch (IOException ex) {
                throw new IllegalArgumentException("dropping problem");
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public Set<String> getTableSet() {
        return tables.keySet();
    }

    private void nameIsNullAssertion(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
    }
}

