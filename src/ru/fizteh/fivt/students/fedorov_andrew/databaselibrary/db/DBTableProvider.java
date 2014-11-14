package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TableCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientCollection;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientMap;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

public class DBTableProvider implements TableProvider {

    public static final char LIST_SEPARATOR_CHARACTER = ',';

    public static final char QUOTE_CHARACTER = '\"';
    public static final char ESCAPE_CHARACTER = '/';
    public static final String QUOTED_STRING_REGEX =
            Utility.getQuotedStringRegex(QUOTE_CHARACTER + "", ESCAPE_CHARACTER + "");

    private static final Collection<Class<?>> SUPPORTED_TYPES = new ConvenientCollection<>(
            new HashSet<Class<?>>()).addNext(Integer.class).addNext(Long.class).addNext(Byte.class)
                                    .addNext(Double.class).addNext(Float.class).addNext(Boolean.class)
                                    .addNext(String.class);

    private static final Map<Class<?>, Function<String, Object>> PARSERS =
            new ConvenientMap<>(new HashMap<Class<?>, Function<String, Object>>())
                    .putNext(Integer.class, Integer::parseInt).putNext(Long.class, Long::parseLong)
                    .putNext(Byte.class, Byte::parseByte).putNext(Boolean.class, Boolean::parseBoolean)
                    .putNext(Double.class, Double::parseDouble).putNext(Float.class, Float::parseFloat)
                    .putNext(
                            String.class,
                            s -> Utility.unquoteString(s, QUOTE_CHARACTER + "", ESCAPE_CHARACTER + ""));

    private final Path databaseRoot;

    /**
     * Mapping between table names and tables. Corrupt tables are null.
     */
    private final Map<String, StoreableTableImpl> tables;

    /**
     * Mapping (table name, last corruption reason). To keep user informed.
     */
    private final Map<String, TableCorruptIOException> corruptTables;

    /**
     * Constructs a database table provider.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     *         If failed to scan database directory.
     */
    DBTableProvider(Path databaseRoot) throws DatabaseIOException {
        this.databaseRoot = databaseRoot;
        this.tables = new HashMap<>();
        this.corruptTables = new HashMap<>();
        reloadTables();
    }

    @Override
    public StoreableTableImpl getTable(String name) throws IllegalArgumentException {
        Utility.checkTableNameIsCorrect(name);
        if (tables.containsKey(name)) {
            StoreableTableImpl table = tables.get(name);
            if (table == null) {
                DatabaseIOException corruptionReason = corruptTables.get(name);
                throw new IllegalArgumentException(corruptionReason.getMessage(), corruptionReason);
            }
            return table;
        } else {
            return null;
        }
    }

    @Override
    public StoreableTableImpl createTable(String name, List<Class<?>> columnTypes)
            throws IllegalArgumentException, DatabaseIOException {
        Utility.checkTableNameIsCorrect(name);

        if (columnTypes == null) {
            throw new IllegalArgumentException("Column types list must not be null");
        }
        if (columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Column types list must not be empty");
        }
        Utility.checkAllTypesAreSupported(columnTypes, SUPPORTED_TYPES);

        Path tablePath = databaseRoot.resolve(name);

        if (tables.containsKey(name) && tables.get(name) != null) {
            return null;
        }

        StoreableTableImpl newTable = StoreableTableImpl.createTable(this, tablePath, columnTypes);
        tables.put(name, newTable);
        return newTable;
    }

    @Override
    public void removeTable(String name)
            throws IllegalArgumentException, IllegalStateException, DatabaseIOException {
        Utility.checkTableNameIsCorrect(name);
        Path tablePath = databaseRoot.resolve(name);

        if (!tables.containsKey(name)) {
            throw new IllegalStateException(name + " not exists");
        }

        StoreableTableImpl removed = tables.remove(name);
        if (removed != null) {
            removed.invalidate();
        }

        corruptTables.remove(name);

        if (!Files.exists(tablePath)) {
            return;
        }

        try {
            Utility.rm(tablePath);
        } catch (IOException exc) {
            // Mark as corrupt.
            tables.put(name, null);

            TableCorruptIOException corruptionReason = new TableCorruptIOException(
                    name, "Failed to drop table: " + exc.toString(), exc);
            corruptTables.put(name, corruptionReason);
            throw corruptionReason;
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        Utility.checkNotNull(table, "Table");
        Utility.checkNotNull(value, "Value");

        String partRegex = "null|true|false|-?[0-9]+(\\.[0-9]+)?";
        partRegex += "|" + QUOTED_STRING_REGEX;
        partRegex = "\\s*(" + partRegex + ")\\s*";
        String regex = "^\\s*\\[" + partRegex + "(," + partRegex + ")*" + "\\]\\s*$";

        if (!value.matches(regex)) {
            throw new ParseException("wrong type (Does not match JSON simple list regular expression)", -1);
        }

        int leftBound = value.indexOf('[');
        int rightBound = value.lastIndexOf(']');

        Storeable storeable = createFor(table);

        int columnsCount = table.getColumnsCount();
        int currentColumn = 0;

        int index = leftBound + 1;

        for (; index < rightBound; ) {
            char currentChar = value.charAt(index);

            if (Character.isSpaceChar(currentChar)) {
                // Space that does not mean anything.

                index++;
            } else if (LIST_SEPARATOR_CHARACTER == currentChar) {
                // Next list element.

                currentColumn++;
                if (currentColumn >= columnsCount) {
                    throw new ParseException(
                            "wrong type (Too many elements in the list; expected: " + columnsCount + ")",
                            index);
                }
                index++;
            } else {
                // Boolean, Number, Null, String.

                // End of element (exclusive).
                int elementEnd;

                if (QUOTE_CHARACTER == currentChar) {
                    // As soon as the given value matches JSON format, closing quotes are guaranteed to
                    // have been found and no exception can be thrown here -> so format 'wrong type(..
                    // .)' support is not necessary here.

                    elementEnd = Utility.findClosingQuotes(
                            value, index + 1, rightBound, QUOTE_CHARACTER, ESCAPE_CHARACTER) + 1;
                } else {
                    elementEnd = value.indexOf(LIST_SEPARATOR_CHARACTER, index + 1);
                    if (elementEnd == -1) {
                        elementEnd = rightBound;
                    }
                }

                // Parsing the value.
                Object elementObj;

                String elementStr = value.substring(index, elementEnd).trim();
                if ("null".equals(elementStr)) {
                    elementObj = null;
                } else {
                    Class<?> elementClass = table.getColumnType(currentColumn);
                    try {
                        elementObj = PARSERS.get(elementClass).apply(elementStr);
                    } catch (RuntimeException exc) {
                        throw new ParseException("wrong type (" + exc.getMessage() + ")", index);
                    }
                }

                storeable.setColumnAt(currentColumn, elementObj);
                index = elementEnd;
            }
        }

        if (currentColumn + 1 != columnsCount) {
            throw new ParseException(
                    "wrong type (Too few elements in the list; expected: " + columnsCount + ")", -1);
        }

        return storeable;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        Utility.checkNotNull(table, "Table");
        Utility.checkNotNull(value, "Value");

        StoreableTableImpl.checkStoreableAppropriate(table, value);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean comma = false;

        for (int col = 0, colsCount = table.getColumnsCount(); col < colsCount; col++) {

            String colValueStr; // If null, write null.

            if (String.class.equals(table.getColumnType(col))) {
                colValueStr = Utility.quoteString(
                        value.getStringAt(col), QUOTE_CHARACTER + "", ESCAPE_CHARACTER + "");
            } else {
                Object colValue = value.getColumnAt(col);
                if (colValue == null) {
                    colValueStr = null;
                } else {
                    colValueStr = colValue.toString();
                }
            }

            if (comma) {
                sb.append(",");
            }
            comma = true;
            sb.append(colValueStr == null ? "null" : colValueStr);
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        Utility.checkNotNull(table, "Table");
        return new StoreableImpl(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        Utility.checkNotNull(table, "Table");
        Utility.checkNotNull(values, "Values list");

        if (table.getColumnsCount() != values.size()) {
            throw new IndexOutOfBoundsException(
                    "Wrong number of values given; expected: " + table.getColumnsCount() + ", actual: "
                    + values.size());
        }

        Storeable storeable = new StoreableImpl(table);

        int column = 0;
        for (Object value : values) {
            storeable.setColumnAt(column++, value);
        }

        return storeable;
    }

    /**
     * Scans database directory and reads all tables from it.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     */
    private void reloadTables() throws DatabaseIOException {
        tables.clear();

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(databaseRoot)) {
            for (Path tablePath : dirStream) {
                String tableName = tablePath.getFileName().toString();

                try {
                    StoreableTableImpl table = StoreableTableImpl.getTable(this, tablePath);
                    tables.put(tableName, table);
                } catch (DatabaseIOException exc) {
                    // mark as corrupt
                    tables.put(tableName, null);
                    corruptTables.put(
                            tableName,
                            (exc instanceof TableCorruptIOException
                             ? (TableCorruptIOException) exc
                             : new TableCorruptIOException(tableName, exc.getMessage(), exc)));
                }
            }
        } catch (IOException exc) {
            throw new DatabaseIOException("Failed to scan database directory", exc);
        }
    }

    /**
     * Returns mapping (table name, table).<br/>
     * {@code Null} is used if a table is corrupt.
     */
    public Set<Entry<String, StoreableTableImpl>> listTables() {
        return tables.entrySet();
    }

}
