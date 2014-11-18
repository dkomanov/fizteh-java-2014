package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.TableRow;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.SyntaxCheckers;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.WrongTableNameException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DbTableProvider implements TableProvider {
    private File rootDir;
    private DbTable currentTable;
    private Map<String, DbTable> tables = new HashMap<>();

    public DbTableProvider(final File rootDir) {
        if (rootDir == null) {
            throw new NullPointerException();
        } else if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new RuntimeException("can't create directory: " + rootDir.getAbsolutePath());
            }
        } else if (!rootDir.isDirectory()) {
            throw new IllegalArgumentException(rootDir.getAbsolutePath() + " isn't a directory");
        }
        this.rootDir = rootDir;
        try {
            loadTables();
        } catch (IOException e) {
            throw new RuntimeException("can't load table names from: " + rootDir.getPath()
                    + ", [" + e.getMessage() + "]");
        }
    }

    public DbTable getCurrentTable() {
        return currentTable;
    }

    public void dump() throws IOException {
        if (currentTable != null) {
            currentTable.commit();
        }
    }

    public boolean containsTable(final String tableName) {
        return tables.containsKey(tableName);
    }

    @Override
    public Table getTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!tables.containsKey(tableName)) {
            return null;
        } else {
            DbTable table = tables.get(tableName);
            if (table == null) {
                tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName).toFile(), this));
            }
            return tables.get(tableName);
        }
    }

    @Override
    public DbTable createTable(final String tableName, final List<Class<?>> columnTypes) throws IOException {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (columnTypes == null) {
            throw new IllegalArgumentException("wrong column types signature");
        } else {
            if (!containsTable(tableName)) {
                Path tablePath = getTablePath(tableName);
                Files.createDirectory(tablePath);
                DbTable table = DbTable.createDbTable(tablePath.toFile(), columnTypes, this);
                tables.put(tableName, table);
                return table;
            } else {
                return null;
            }
        }
    }

    @Override
    public void removeTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!containsTable(tableName)) {
            throw new IllegalStateException("there is no tables with name \"" + tableName + "\"");
        } else {
            if (currentTable != null && currentTable.getName().equals(tableName)) {
                currentTable = null;
            }
            tables.remove(tableName);
            try {
                purgeTable(getTablePath(tableName));
            } catch (IOException e) {
                throw new RuntimeException("can't remove table: " + e.getMessage());
            }
        }
    }

    @Override
    public Storeable deserialize(final Table table, final String value) throws ParseException {
        String str = value.trim();
        if (str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']') {
            throw new ParseException("invalid borders", 0);
        } else {
            str = str.replace("[", "");
            str = str.replace("]", "");
            String[] columns = str.split(",");
            if (columns.length != table.getColumnsCount()) {
                throw new ParseException("sizes of table and value don't correspond", 0);
            }
            List<Object> values = new LinkedList<>();
            for (int i = 0; i < values.size(); ++i) {
                columns[i] = columns[i].trim();
                values.add(parseColumn(table.getColumnType(i), columns[i]));
            }
            return new TableRow(values);
        }
    }

    private Object parseColumn(final Class<?> tableColumnType, String column) throws ParseException {
        try {
            switch (DbTable.getStringNameByType(tableColumnType)) {
                case "int":
                    return new Integer(column);
                case "long":
                    return new Long(column);
                case "byte":
                    return new Byte(column);
                case "float":
                    return new Float(column);
                case "double":
                    return new Double(column);
                case "boolean":
                    return new Boolean(column);
                case "String":
                    return column;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            throw new ParseException("types incompatibility", 0);
        }
    }

    @Override
    public String serialize(final Table table, final Storeable value) throws ColumnFormatException {
        List<String> strColumns = new LinkedList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            Class<?> tableColumnType = table.getColumnType(i);
            Class<?> valueColumnType = value.getColumnAt(i).getClass();
            if (!tableColumnType.equals(valueColumnType)) {
                throw new ColumnFormatException();
            } else {
                strColumns.add(value.getColumnAt(i).toString());
            }
        }
        StringBuilder b = new StringBuilder(String.join(", ", strColumns));
        b.insert(0, "[");
        b.append("]");
        return b.toString();
    }

    @Override
    public Storeable createFor(final Table table) {
        List<Object> storeableValues = new LinkedList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            storeableValues.add(null);
        }
        return new TableRow(storeableValues);
    }

    @Override
    public Storeable createFor(final Table table, final List<?> values) throws ColumnFormatException,
            IndexOutOfBoundsException {
        List<Object> storeableValues = new LinkedList<>();
        for (int i = 0; i < values.size(); ++i) {
            if (!table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException();
            } else {
                storeableValues.add(table.getColumnType(i));
            }
        }
        return new TableRow(storeableValues);
    }

    @Override
    public List<String> getTableNames() {
        List<String> res = new LinkedList<>();
        res.addAll(tables.keySet());
        return res;
    }

    /**
     * @param tableName
     * @return  0 if succeed, otherwise returns number of uncommitted changes in currentTable
     * If number of uncommitted changes isn't zero then table isn't changing.
     * @throws IOException if it is impossible to dump current table.
     */
    public int useTable(final String tableName) {
        if (!SyntaxCheckers.checkCorrectnessOfTableName(tableName)) {
            throw new WrongTableNameException(tableName);
        } else if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException(tableName + " doesn't exist");
        } else {
            if (currentTable == null) {
                currentTable = tables.get(tableName);
                return 0;
            } else {
                boolean currentAndNewTablesAreDistinct = !currentTable.getName().equals(tableName);
                if (currentAndNewTablesAreDistinct) {
                    if (currentTable.getNumberOfUncommittedChanges() == 0) {
                        currentTable = tables.get(tableName);
                        return 0;
                    } else {
                        return currentTable.getNumberOfUncommittedChanges();
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    public Map<String, Integer> showTables() {
        Map<String, Integer> res = new HashMap<>();
        for (String tableName : tables.keySet()) {
            if (tables.get(tableName) == null) {
                tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName).toFile(), this));
            }
            res.put(tableName, tables.get(tableName).size());
        }
        return res;
    }

    private void purgeTable(final Path tablePath) throws IOException {
        File[] files = new File(tablePath.toString()).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                purgeDir(file);
            } else {
                Files.delete(file.toPath());
            }
        }
        Files.delete(tablePath);
    }

    private void purgeDir(final File dir) throws IOException {
        File[] files = dir.listFiles();
        for (File file : files) {
            Files.delete(file.toPath());
        }
        Files.delete(dir.toPath());
    }

    private Path getTablePath(final String tableName) {
        return Paths.get(rootDir.getAbsolutePath(), tableName).normalize();
    }

    private void loadTables() throws IOException {
        String[] tableNames = rootDir.list();
        tables.clear();
        for (String name : tableNames) {
            tables.put(name, null);
        }
    }
}
