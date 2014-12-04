package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TableCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONMaker;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParsedObject;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.json.JSONParser;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientCollection;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientMap;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController.KillLock;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ValidityController.UseLock;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;

final class DBTableProvider implements AutoCloseableProvider {
    public static final char ESCAPE_CHARACTER = '\\';
    private static final char QUOTE_CHARACTER = '\"';
    //    public static final String QUOTED_STRING_REGEX =
    //            Utility.getQuotedStringRegex(QUOTE_CHARACTER + "", ESCAPE_CHARACTER + "" +
    // ESCAPE_CHARACTER);
    private static final Collection<Class<?>> SUPPORTED_TYPES = new ConvenientCollection<>(
            new HashSet<Class<?>>()).addNext(Integer.class).addNext(Long.class).addNext(Byte.class)
                                    .addNext(Double.class).addNext(Float.class).addNext(Boolean.class)
                                    .addNext(String.class);

    private static final Map<Class<?>, Function<String, Object>> PARSERS =
            new ConvenientMap<>(new HashMap<Class<?>, Function<String, Object>>())
                    .putNext(Integer.class, Integer::parseInt).putNext(Long.class, Long::parseLong)
                    .putNext(Byte.class, Byte::parseByte).putNext(
                    Boolean.class, str -> {
                        Utility.checkNotNull(str, "String to parse");
                        if (str.matches("(?i)true|false")) {
                            return Boolean.parseBoolean(str);
                        } else {
                            throw new ColumnFormatException("Expected 'true' or 'false' as boolean");
                        }
                    }).putNext(Double.class, Double::parseDouble).putNext(Float.class, Float::parseFloat)
                    .putNext(
                            String.class, str -> {
                                if (!str.startsWith(QUOTE_CHARACTER + "") || !str
                                        .endsWith(QUOTE_CHARACTER + "")) {
                                    throw new ColumnFormatException("(String expected to be in quotes");
                                }
                                return str.substring(1, str.length() - 1);
                            });

    private final Path databaseRoot;
    /**
     * Mapping between table names and tables. Corrupt tables are null.
     */
    private final Map<String, AutoCloseableTable> tables;
    /**
     * Mapping (table name, last corruption reason). To keep user informed.
     */
    private final Map<String, TableCorruptIOException> corruptTables;
    /**
     * Lock for getting/creating/removing tables access management.
     */
    private final ReadWriteLock persistenceLock = new ReentrantReadWriteLock(true);
    private final ValidityController validityController = new ValidityController();
    private final DBTableProviderFactory factory;
    /**
     * Special flag that prevents from reacting on event raised by this object.
     */
    private boolean tableClosedByMe = false;

    /**
     * Constructs a database table provider.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     *         If failed to scan database directory.
     */
    DBTableProvider(Path databaseRoot, DBTableProviderFactory factory) throws DatabaseIOException {
        this.databaseRoot = databaseRoot;
        this.factory = factory;
        this.tables = new HashMap<>();
        this.corruptTables = new HashMap<>();
        reloadAllTables();
    }

    @Override
    public AutoCloseableTable getTable(String name) throws IllegalArgumentException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkTableNameIsCorrect(name);

            Lock lock = persistenceLock.readLock();
            lock.lock();
            try {
                if (!tables.containsKey(name)) {
                    // Read table from FS. Must get a better lock.
                    lock.unlock();
                    lock = persistenceLock.writeLock();
                    lock.lock();
                    if (!tables.containsKey(name)) {
                        try {
                            loadMissingTables();
                        } catch (DatabaseIOException exc) {
                            throw new IllegalArgumentException(exc.getMessage(), exc);
                        }
                    }
                }

                if (tables.containsKey(name)) {
                    AutoCloseableTable table = tables.get(name);
                    if (table == null) {
                        // Table is corrupt.
                        DatabaseIOException corruptionReason = corruptTables.get(name);
                        throw new IllegalArgumentException(
                                corruptionReason.getMessage(), corruptionReason);
                    }

                    // Table is normal.
                    return table;
                } else {
                    // Table not exists.
                    return null;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public AutoCloseableTable createTable(String name, List<Class<?>> columnTypes)
            throws IllegalArgumentException, DatabaseIOException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkTableNameIsCorrect(name);

            if (columnTypes == null) {
                throw new IllegalArgumentException("Column types list must not be null");
            }
            if (columnTypes.isEmpty()) {
                throw new IllegalArgumentException("Column types list must not be empty");
            }
            Utility.checkAllTypesAreSupported(columnTypes, SUPPORTED_TYPES);

            Path tablePath = databaseRoot.resolve(name);

            persistenceLock.writeLock().lock();
            try {
                if (tables.containsKey(name) && tables.get(name) != null) {
                    return null;
                }

                AutoCloseableTable newTable =
                        StoreableTableImpl.createTable(this, this::onTableClosed, tablePath, columnTypes);
                tables.put(name, newTable);
                return newTable;
            } finally {
                persistenceLock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeTable(String name)
            throws IllegalArgumentException, IllegalStateException, DatabaseIOException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkTableNameIsCorrect(name);
            Path tablePath = databaseRoot.resolve(name);

            persistenceLock.writeLock().lock();
            try {
                if (!tables.containsKey(name)) {
                    throw new IllegalStateException(name + " not exists");
                }

                AutoCloseableTable removed = tables.remove(name);
                if (removed != null) {
                    // After invalidation all attempts to commit from other threads fail with
                    // IllegalStateException. Now we can delete the table without fear that it will be written
                    // to the file system again.
                    removed.close();
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
            } finally {
                persistenceLock.writeLock().unlock();
            }
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkNotNull(table, "Table");
            Utility.checkNotNull(value, "Value");

            //            String partRegex = "null|true|false|-?[0-9]+(\\.[0-9]+)?";
            //            partRegex += "|" + QUOTED_STRING_REGEX;
            //            partRegex = "\\s*(" + partRegex + ")\\s*";
            //            String regex = "^\\s*\\[" + partRegex + "(," + partRegex + ")*" + "\\]\\s*$";
            //
            //            if (!value.matches(regex)) {
            //                throw new ParseException(
            //                        "wrong type (Does not match JSON simple list regular expression)", -1);
            //            }
            //
            int leftBound = value.indexOf('[');
            int rightBound = value.lastIndexOf(']');

            if (leftBound < 0 || rightBound < 0) {
                throw new ParseException("wrong type (Arguments must be inside square brackets)", -1);
            }

            Storeable storeable = createFor(table);

            JSONParsedObject parsedObject;
            try {
                parsedObject = JSONParser.parseJSON(value.substring(leftBound, rightBound + 1));
            } catch (ParseException exc) {
                throw new ParseException("wrong type (" + exc.getMessage() + ")", exc.getErrorOffset());
            }
            if (!parsedObject.isStandardArray()) {
                throw new ParseException("wrong type (Arguments must be given as array)", -1);
            }

            Object[] args = parsedObject.asArray();

            if (args.length != table.getColumnsCount()) {
                throw new ParseException("wrong type (Irregular number of arguments given)", -1);
            }

            try {
                for (int i = 0; i < args.length; i++) {
                    Object elementObj;

                    if (args[i] == null) {
                        elementObj = null;
                    } else if (args[i] instanceof JSONParsedObject) {
                        throw new ParseException("wrong type (Complex types are not supported)", i);
                    } else {
                        String str = args[i].toString();
                        if (args[i] instanceof String) {
                            str = QUOTE_CHARACTER + str + QUOTE_CHARACTER;
                        }

                        elementObj = PARSERS.get(table.getColumnType(i)).apply(str);
                    }

                    storeable.setColumnAt(i, elementObj);
                }
            } catch (RuntimeException exc) {
                throw new ParseException("wrong type (" + exc.getMessage() + ")", -1);
            }

            return storeable;
            //            int currentColumn = 0;
            //
            //            int index = leftBound + 1;
            //
            //            for (; index < rightBound; ) {
            //                char currentChar = value.charAt(index);
            //
            //                if (Character.isSpaceChar(currentChar)) {
            //                    // Space that does not mean anything.
            //
            //                    index++;
            //                } else if (LIST_SEPARATOR_CHARACTER == currentChar) {
            //                    // Next list element.
            //
            //                    currentColumn++;
            //                    if (currentColumn >= columnsCount) {
            //                        throw new ParseException(
            //                                "wrong type (Too many elements in the list; expected: " +
            // columnsCount + ")",
            //                                index);
            //                    }
            //                    index++;
            //                } else {
            //                    // Boolean, Number, Null, String.
            //
            //                    // End of element (exclusive).
            //                    int elementEnd;
            //
            //                    if (QUOTE_CHARACTER == currentChar) {
            //                        // As soon as the given value matches JSON format, closing quotes
            //                        // are guaranteed to
            //                        // have been found and no exception can be thrown here -> so format
            //                        // 'wrong type(..
            //                        // .)' support is not necessary here.
            //
            //                        elementEnd = Utility.findClosingQuotes(
            //                                value, index + 1, rightBound, QUOTE_CHARACTER,
            // ESCAPE_CHARACTER) + 1;
            //                    } else {
            //                        elementEnd = value.indexOf(LIST_SEPARATOR_CHARACTER, index + 1);
            //                        if (elementEnd == -1) {
            //                            elementEnd = rightBound;
            //                        }
            //                    }
            //
            //                    // Parsing the value.
            //                    Object elementObj;
            //
            //                    String elementStr = value.substring(index, elementEnd).trim();
            //                    if ("null".equals(elementStr)) {
            //                        elementObj = null;
            //                    } else {
            //                        Class<?> elementClass = table.getColumnType(currentColumn);
            //                        try {
            //                            elementObj = PARSERS.get(elementClass).apply(elementStr);
            //                        } catch (RuntimeException exc) {
            //                            throw new ParseException(
            //                                    "wrong type (" + exc.getMessage() + ")", index);
            //                        }
            //                    }
            //
            //                    storeable.setColumnAt(currentColumn, elementObj);
            //                    index = elementEnd;
            //                }
            //            }
            //
            //            if (currentColumn + 1 != columnsCount) {
            //                throw new ParseException(
            //                        "wrong type (Too few elements in the list; expected: " + columnsCount
            // + ")", -1);
            //            }
            //
            //            return storeable;
        }
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try (UseLock useLock = validityController.use()) {
            Utility.checkNotNull(table, "Table");
            Utility.checkNotNull(value, "Value");

            StoreableTableImpl.checkStoreableAppropriate(table, value);

            if (value instanceof StoreableImpl) {
                // Optimization: we do not create new array.
                return JSONMaker.makeJSON(value);
            } else {
                Object[] values = new Object[table.getColumnsCount()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = value.getColumnAt(i);
                }
                return JSONMaker.makeJSON(values);
            }
        }
    }

    @Override
    public Storeable createFor(Table table) {
        try (UseLock useLock = validityController.use()) {
            Utility.checkNotNull(table, "Table");
            return new StoreableImpl(table);
        }
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        try (UseLock useLock = validityController.use()) {
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
    }

    @Override
    public List<String> getTableNames() {
        try (UseLock useLock = validityController.use()) {
            persistenceLock.readLock().lock();
            try {
                return new LinkedList<>(tables.keySet());
            } finally {
                persistenceLock.readLock().unlock();
            }
        }
    }

    /**
     * Loads table from the given path. Not thread-safe.
     */
    private void loadTable(Path tablePath) {
        String tableName = tablePath.getFileName().toString();

        try {
            AutoCloseableTable table = StoreableTableImpl.getTable(this, this::onTableClosed, tablePath);
            tables.put(tableName, table);
        } catch (DatabaseIOException exc) {
            // Mark as corrupt.
            tables.put(tableName, null);
            corruptTables.put(
                    tableName,
                    (exc instanceof TableCorruptIOException
                     ? (TableCorruptIOException) exc
                     : new TableCorruptIOException(tableName, exc.getMessage(), exc)));
        }
    }

    /**
     * Loads tables from file system that are not registered.<br/>
     * Not thread-safe.
     * @throws DatabaseIOException
     */
    private void loadMissingTables() throws DatabaseIOException {
        loadTables(tables::containsKey);
    }

    private void reloadAllTables() throws DatabaseIOException {
        tables.clear();
        corruptTables.clear();
        loadTables((tableName) -> true);
    }

    /**
     * Scans database directory and reads all tables from it. Not thread-safe.
     * @throws ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException
     */
    private void loadTables(Predicate<String> loadFilter) throws DatabaseIOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(databaseRoot)) {
            for (Path tablePath : dirStream) {
                if (loadFilter.test(tablePath.getFileName().toString())) {
                    loadTable(tablePath);
                }
            }
        } catch (IOException exc) {
            throw new DatabaseIOException("Failed to scan database directory", exc);
        }
    }

    /**
     * The given table is dismissed.
     * @param table
     *         link to the closed table (no matter if it is proxy or pure).
     */
    void onTableClosed(Table table) {
        try (UseLock useLock = validityController.use()) {
            if (tableClosedByMe) {
                return;
            }
            persistenceLock.writeLock().lock();
            try {
                tables.remove(table.getName());
            } finally {
                persistenceLock.writeLock().unlock();
            }
        }
    }

    @Override
    public String toString() {
        try (UseLock lock = validityController.use()) {
            return DBTableProvider.class.getSimpleName() + "[" + databaseRoot + "]";
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public void close() {
        try (KillLock lock = validityController.useAndKill()) {
            tableClosedByMe = true;
            persistenceLock.writeLock().lock();
            try {
                tables.values().stream().filter(table -> table != null).forEach(AutoCloseableTable::close);
                tables.clear();
                corruptTables.clear();
            } finally {
                persistenceLock.writeLock().unlock();
            }
            factory.onProviderClosed(this);
        } finally {
            tableClosedByMe = false;
        }
    }
}
