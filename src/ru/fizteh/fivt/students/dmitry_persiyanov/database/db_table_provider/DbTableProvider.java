package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.TableLoaderDumper;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.TableRow;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.SyntaxCheckers;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.TypeStringTranslator;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.utils.Utility;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.WrongTableNameException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class DbTableProvider implements TableProvider {
    private Path rootDir;
    private DbTable currentTable;
    private Map<String, DbTable> tables = new HashMap<>();

    public DbTableProvider(final Path rootDir) {
        if (rootDir == null) {
            throw new NullPointerException();
        } else if (!Files.exists(rootDir)) {
            try {
                Files.createDirectories(rootDir);
            } catch (IOException e) {
                throw new RuntimeException("can't create directory: " + rootDir.toString());
            }
        } else if (!Files.isDirectory(rootDir)) {
            throw new IllegalArgumentException(rootDir.toString() + " isn't a directory");
        }
        this.rootDir = rootDir;
        try {
            loadTables();
        } catch (IOException e) {
            throw new RuntimeException("can't load tables from: " + rootDir.toString()
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

    private boolean containsTable(final String tableName) {
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
                tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName), this));
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
                DbTable table = DbTable.createDbTable(tablePath, columnTypes, this);
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
//        String stringRegex = "'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\""
//                + "([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"";
        String stringRegex = "\"([^\"]*)\"";
        String oneColumnTypeRegex = "\\s*(" + stringRegex + "|null|true|false|-?\\d+(\\.\\d+)?)\\s*";
        String jsonRegex = "^\\[" + oneColumnTypeRegex + "(," + oneColumnTypeRegex + ")*\\]$";
        if (!str.matches(jsonRegex)) {
            throw new ParseException("value isn't in JSON format", 0);
        } else {
            try {
                int leftBracket = str.indexOf('[');
                int rightBracket = str.lastIndexOf(']');
                List<Object> values = new LinkedList<>();
                int i = leftBracket + 1;
                while (i < rightBracket) {
                    char currChar = str.charAt(i);
                    if (currChar == '\"') {
                        // String argument. Finding end quote.
                        int endQuoteIndex = i + 1;
                        while (!(str.charAt(endQuoteIndex) == '\"' && str.charAt(endQuoteIndex - 1) != '\\')) {
                            endQuoteIndex++;
                        }
                        String strColumn = str.substring(i + 1, endQuoteIndex);
                        values.add(strColumn);
                        i = endQuoteIndex + 1;
                    } else if (Character.isSpaceChar(currChar) || currChar == ',') {
                        i++;
                    } else if (Character.isDigit(currChar) || currChar == '-') {
                        int nextComma = str.indexOf(',', i);
                        if (nextComma == -1) {
                            // Last column.
                            nextComma = rightBracket;
                        }
                        String number = str.substring(i, nextComma).trim();
                        Class<?> tableColType = table.getColumnType(values.size());
                        if (number.indexOf('.') != -1) {
                            if (tableColType.equals(Double.class)) {
                                values.add(new Double(number));
                            } else if (tableColType.equals(Float.class)) {
                                values.add(new Float(number));
                            }
                        } else {
                            if (tableColType.equals(Integer.class)) {
                                values.add(new Integer(number));
                            } else if (tableColType.equals(Long.class)) {
                                values.add(new Long(number));
                            } else if (tableColType.equals(Double.class)) {
                                values.add(new Double(number));
                            } else if (tableColType.equals(Float.class)) {
                                values.add(new Float(number));
                            }
                        }
                        i = nextComma + 1;
                    } else {
                        // Boolean or null
                        int nextComma = str.indexOf(',', i);
                        if (nextComma == -1) {
                            nextComma = rightBracket;
                        }
                        String boolOrNullValue = str.substring(i, nextComma).trim();
                        if (boolOrNullValue.equals("true")) {
                            values.add(true);
                        } else if (boolOrNullValue.equals("false")) {
                            values.add(false);
                        } else if (boolOrNullValue.equals("null")) {
                            values.add(null);
                        } else {
                            throw new ParseException("it's not possible, but there is a parse error!", 0);
                        }
                        i = nextComma + 1;
                    }
                }
                if (values.size() != table.getColumnsCount()) {
                    throw new ParseException("incompatible sizes of Storeable in the table and json you passed", 0);
                }
                return createFor(table, values);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("can't parse your json", 0);
            } catch (NumberFormatException e) {
                throw new ParseException("types incompatibility", 0);
            }
        }
    }

    @Override
    public String serialize(final Table table, final Storeable value) throws ColumnFormatException {
        if (value == null) {
            return null;
        } else {
            List<String> strColumns = new LinkedList<>();
            for (int i = 0; i < table.getColumnsCount(); ++i) {
                if (value.getColumnAt(i) == null) {
                    strColumns.add(null);
                } else {
                    Class<?> tableColumnType = table.getColumnType(i);
                    Class<?> valueColumnType = value.getColumnAt(i).getClass();
                    if (!tableColumnType.equals(valueColumnType)) {
                        throw new ColumnFormatException();
                    } else if (valueColumnType.equals(String.class)) {
                        strColumns.add("\"" + value.getColumnAt(i).toString() + "\"");
                    } else {
                        strColumns.add(value.getColumnAt(i).toString());
                    }
                }
            }
            StringBuilder b = new StringBuilder(String.join(", ", strColumns));
            b.insert(0, "[");
            b.append("]");
            return b.toString();
        }
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
            if (values.get(i) != null && !table.getColumnType(i).equals(values.get(i).getClass())) {
                throw new ColumnFormatException("types incompatibility, needed type: "
                        + TypeStringTranslator.getStringNameByType(table.getColumnType(i)) + ", passed type: "
                        + TypeStringTranslator.getStringNameByType(values.get(i).getClass()));
            } else {
                storeableValues.add(values.get(i));
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
                if (tables.get(tableName) == null) {
                    tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName), this));
                }
                currentTable = tables.get(tableName);
                return 0;
            } else {
                boolean currentAndNewTablesAreDistinct = !currentTable.getName().equals(tableName);
                if (currentAndNewTablesAreDistinct) {
                    if (currentTable.getNumberOfUncommittedChanges() == 0) {
                        if (tables.get(tableName) == null) {
                            tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName), this));
                        }
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
                tables.put(tableName, DbTable.loadExistingDbTable(getTablePath(tableName), this));
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
        return rootDir.resolve(tableName);
    }

    private void loadTables() throws IOException {
        tables.clear();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir)) {
            for (Path tableDir : stream) {
                TableLoaderDumper.checkTableForCorruptness(tableDir);
                tables.put(Utility.getNameByPath(tableDir), null);
            }
        }
    }
}
