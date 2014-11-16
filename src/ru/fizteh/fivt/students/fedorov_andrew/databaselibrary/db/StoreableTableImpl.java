package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ImproperStoreableException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.TableCorruptIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.ConvenientMap;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.TestBase;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StoreableTableImpl implements Table {
    private static final Map<Class<?>, String> CLASSES_TO_NAMES_MAP =
            new ConvenientMap<>(new HashMap<Class<?>, String>()).putNext(Integer.class, "int")
                                                                .putNext(Long.class, "long")
                                                                .putNext(Byte.class, "byte")
                                                                .putNext(Double.class, "double")
                                                                .putNext(Float.class, "float")
                                                                .putNext(Boolean.class, "boolean")
                                                                .putNext(String.class, "String");

    private static final Map<String, Class<?>> NAMES_TO_CLASSES_MAP =
            Utility.inverseMap(CLASSES_TO_NAMES_MAP);

    private static final String COLUMNS_FORMAT_FILENAME = "signature.tsv";

    private final TableProvider provider;

    private final StringTableImpl store;

    private final List<Class<?>> columnTypes;

    private boolean invalidated;

    private StoreableTableImpl(TableProvider provider, StringTableImpl store, List<Class<?>> columnTypes) {
        this.provider = provider;
        this.invalidated = false;
        this.store = store;
        this.columnTypes = Collections.unmodifiableList(new ArrayList<Class<?>>(columnTypes));
    }

    static StoreableTableImpl createTable(TableProvider provider, Path tablePath, List<Class<?>> columnTypes)
            throws DatabaseIOException {
        // Creating storage.
        StringTableImpl store = StringTableImpl.createTable(tablePath);

        // Filling signature file.
        try (PrintWriter writer = new PrintWriter(tablePath.resolve(COLUMNS_FORMAT_FILENAME).toString())) {
            String[] types = columnTypes.stream().map(CLASSES_TO_NAMES_MAP::get).toArray(String[]::new);
            writer.print(String.join(" ", types));
        } catch (IOException exc) {
            try {
                Utility.rm(store.getTableRoot());
            } catch (Throwable thr) {
                Log.log(StoreableTableImpl.class, exc, "Failed to cleanup after table creation failure");
            }

            throw new DatabaseIOException(
                    "Failed to create table types description file: " + exc.toString(), exc);
        }

        return new StoreableTableImpl(provider, store, columnTypes);
    }

    /**
     * Converts a list of types split by space characters into a list of classes.
     * @param typesString
     *         List of types.
     * @return List of classes. Order is honoured.
     * @throws IllegalArgumentException
     *         If one of types is not supported by database engine.
     */
    public static List<Class<?>> parseColumnTypes(String typesString) throws IllegalArgumentException {
        String[] types = typesString.split("\\s+");

        List<Class<?>> columnTypes = new LinkedList<>();

        Arrays.asList(types).stream().map(
                type -> {
                    Class<?> clazz = NAMES_TO_CLASSES_MAP.get(type);
                    if (clazz == null) {
                        throw new IllegalArgumentException("wrong type (" + type + " not supported)");
                    }
                    return clazz;
                }).forEachOrdered(columnTypes::add);

        return columnTypes;
    }

    static StoreableTableImpl getTable(TableProvider provider, Path tablePath) throws DatabaseIOException {
        StringTableImpl store = StringTableImpl
                .getTable(tablePath, path -> path != null && path.toString().equals(COLUMNS_FORMAT_FILENAME));
        List<Class<?>> columnTypes;

        // Reading column types from signature file.
        try (Scanner scanner = new Scanner(store.getTableRoot().resolve(COLUMNS_FORMAT_FILENAME))) {
            String typesString = scanner.nextLine();

            columnTypes = parseColumnTypes(typesString);
        } catch (IOException exc) {
            throw new TableCorruptIOException(store.getName(), "Failed to open types description file", exc);
        } catch (IllegalArgumentException exc) {
            // Honouring 'wrong-type' format.
            if (exc.getMessage().matches(TestBase.WRONG_TYPE_MESSAGE_REGEX)) {
                String msg = exc.getMessage();
                String descriptionPart = msg.substring(msg.indexOf('('), msg.lastIndexOf(')'));
                throw new DatabaseIOException(
                        "wrong type (Invalid type description file for table " + store.getName() + ": "
                        + descriptionPart + ")");
            } else {
                throw new TableCorruptIOException(
                        store.getName(), "Invalid type description file: " + exc.getMessage());
            }
        }

        StoreableTableImpl table = new StoreableTableImpl(provider, store, columnTypes);

        // Checking that all stored values are of proper type.
        List<String> keys = table.list();

        for (String key : keys) {
            try {
                table.get0(key);
            } catch (ImproperStoreableException exc) {
                throw new TableCorruptIOException(
                        store.getName(), "Value of improper format found: " + store.get(key));
            }
        }

        return table;
    }

    /**
     * Checks whether the given storeable can be stored in the given table as a value.
     * @throws ColumnFormatException
     *         If columns count differs or some column has wrong type. Note that if some column has null
     *         value, its type cannot be determined.
     * @throws java.lang.IllegalStateException
     *         If the given storeable is already assigned to another table. This check can be performed only
     *         for instances of {@link StoreableTableImpl}.
     */
    public static void checkStoreableAppropriate(Table table, Storeable storeable)
            throws ColumnFormatException, IllegalStateException {
        Utility.checkNotNull(table, "Table");
        Utility.checkNotNull(storeable, "Value");

        // Checking number of elements, stored in storeable.
        try {
            storeable.getColumnAt(table.getColumnsCount() - 1);
        } catch (IndexOutOfBoundsException exc) {
            throw new ColumnFormatException(
                    "wrong type (Got less columns then expected)");
        }

        try {
            storeable.getColumnAt(table.getColumnsCount());
            throw new ColumnFormatException(
                    "wrong type (Got more columns then expected)");
        } catch (IndexOutOfBoundsException exc) {
            // It should be caught and ignored here for normal work.
        }

        // Checking column types where possible.
        for (int column = 0, columnsCount = table.getColumnsCount(); column < columnsCount; column++) {
            Class<?> expectedType = table.getColumnType(column);
            Object element = storeable.getColumnAt(column);

            if (element != null) {
                Class<?> actualType = element.getClass();
                if (!expectedType.equals(actualType)) {
                    throw new ColumnFormatException(
                            String.format(
                                    "wrong type (Column #%d expected to have type %s, but actual"
                                    + " type is %s)",
                                    column,
                                    expectedType.getSimpleName(),
                                    actualType.getSimpleName()));
                }
            }
        }

        if (storeable instanceof StoreableImpl) {
            if (((StoreableImpl) storeable).getHost() != table) {
                throw new IllegalStateException(
                        "Cannot put storeable assigned to one table to another table");
            }
        }
    }

    TableProvider getProvider() {
        return provider;
    }

    /**
     * Counts uncommitted changes - diff between last committed (or read) version and current version.
     */
    public int getUncommittedChangesCount() {
        checkValidity();
        return store.getUncommittedChangesCount();
    }

    /**
     * Mark this table as invalidated (all further operations throw {@link java.lang.IllegalStateException}).
     */
    void invalidate() {
        invalidated = true;
    }

    private void checkValidity() throws IllegalStateException {
        if (invalidated) {
            throw new IllegalStateException(store.getName() + " is invalidated");
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkValidity();
        Utility.checkNotNull(key, "Key");
        Utility.checkNotNull(value, "Value");
        checkStoreableAppropriate(this, value);

        Storeable previousValue = get0(key);
        String serialized = provider.serialize(this, value);
        store.put(key, serialized);
        return previousValue;
    }

    @Override
    public Storeable remove(String key) {
        checkValidity();
        Utility.checkNotNull(key, "Key");

        Storeable previousValue = get0(key);
        store.remove(key);
        return previousValue;
    }

    @Override
    public int size() {
        checkValidity();
        return store.size();
    }

    @Override
    public int commit() throws DatabaseIOException {
        checkValidity();
        return store.commit();
    }

    @Override
    public int rollback() {
        checkValidity();
        return store.rollback();
    }

    @Override
    public int getColumnsCount() {
        checkValidity();
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkValidity();
        if (columnIndex < 0 || columnIndex >= getColumnsCount()) {
            throw new IndexOutOfBoundsException(
                    "columnIndex must be between zero (inclusive) and columnsCount (exclusive)");
        }
        return columnTypes.get(columnIndex);
    }

    /**
     * Collects all keys from all table parts assigned to this table.
     */
    public List<String> list() {
        checkValidity();
        return store.list();
    }

    @Override
    public String getName() {
        checkValidity();
        return store.getName();
    }

    @Override
    public Storeable get(String key) {
        checkValidity();
        Utility.checkNotNull(key, "Key");
        return get0(key);
    }

    /**
     * Get Storeable value by key. All checks are supposed to have been performed.
     */
    private Storeable get0(String key) throws ImproperStoreableException {
        String valueStr = store.get(key);
        if (valueStr == null) {
            return null;
        }

        try {
            return provider.deserialize(this, valueStr);
        } catch (ParseException exc) {
            // This case can occur only during table check (after reading table).
            throw new ImproperStoreableException(exc.getMessage(), exc);
        }
    }
}
