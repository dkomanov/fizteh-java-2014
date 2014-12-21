package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableHolder implements TableProvider {
    private Path rootPath;
    private Map<String, DBTable> tableMap;

    public TableHolder(final String rootDir) throws IOException {
        try {
            rootPath = Paths.get(rootDir);
            if (!Files.exists(rootPath)) {
                Files.createDirectory(rootPath);
            }
            if (!Files.isDirectory(rootPath)) {
                throw new IllegalArgumentException(rootDir
                        + " is not directory");
            }
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException(rootDir
                    + "' is illegal directory name", e);
        }
        tableMap = new HashMap<>();
        Utility.checkDirectorySubdirectories(rootPath);
        try (DirectoryStream<Path> databaseStream = Files.newDirectoryStream(rootPath)) {
            for (Path tableDirectory : databaseStream) {
                String tableName = tableDirectory.getFileName().toString();
                tableMap.put(tableName, new DBTable(rootPath, tableName, this));
            }
        }
    }

    public final void close() throws IOException {
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().commit();
            }
        }
        tableMap.clear();
    }

    @Override
    public Table getTable(String name) {
        Utility.checkTableName(name);
        String tableName = name;
        if (tableMap.containsKey(tableName)) {
            if (tableMap.get(tableName) == null) {
                throw new IllegalStateException("Table was removed");
            }
            return tableMap.get(tableName);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        Utility.checkTableName(name);
        Utility.checkTableColumnTypes(columnTypes);
        if (tableMap.get(name) == null) {
            Path pathTableDirectory = rootPath.resolve(name);
            Files.createDirectory(pathTableDirectory);
            DBTable newTable = new DBTable(rootPath,
                    name, new HashMap<>(), columnTypes, this);
            Path tableSignaturePath = pathTableDirectory.resolve(Utility.TABLE_SIGNATURE);
            Files.createFile(tableSignaturePath);

            try (RandomAccessFile writeSig = new RandomAccessFile(tableSignaturePath.toString(), "rw")) {
                for (Class type : columnTypes) {
                    String s = Utility.WRAPPERS_TO_PRIMITIVE.get(type) + " ";
                    writeSig.write(s.getBytes(Utility.ENCODING));
                }
            }
            tableMap.put(name, newTable);
            return newTable;
        } else {
            return null;
        }
    }

    @Override
    public void removeTable(String name) throws IOException {
        Utility.checkTableName(name);
        if (tableMap.get(name) == null) {
            throw new IllegalStateException(name + " doesn't exist");
        } else {
            Path tableDirectory = tableMap.get(name).getDBTablePath();
            Utility.recursiveDeleteCopy(tableDirectory);
            tableMap.put(name, null);
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<String> values = Utility.parseString(value);
        List<Object> tableValues = Utility.formatStringValues(table, values);
        try {
            return createFor(table, tableValues);
        } catch (ColumnFormatException | IndexOutOfBoundsException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    @Override
    public String serialize(Table table, Storeable value) {
        List<String> storeableValues = Utility.getStoreableValues(table, value);
        String joined = String.join(Utility.FORMATTER, storeableValues);
        StringBuilder result = new StringBuilder();
        result.append(Utility.VALUE_START_LIMITER);
        result.append(joined);
        result.append(Utility.VALUE_END_LIMITER);
        return result.toString();
    }

    @Override
    public Storeable createFor(Table table) {
        int size = table.getColumnsCount();
        List<Class<?>> requiredTypes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            requiredTypes.add(table.getColumnType(i));
        }
        return new Record(requiredTypes);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) {
        Storeable aimedRecord = createFor(table);
        int size = table.getColumnsCount();
        if (values.size() != size) {
            throw new IndexOutOfBoundsException("wrong amount of columns");
        } else {
            for (int i = 0; i < size; i++) {
                Object currentValue = values.get(i);
                aimedRecord.setColumnAt(i, currentValue);
            }
            return aimedRecord;
        }
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (Map.Entry<String, DBTable> entry : tableMap.entrySet()) {
            if (!(entry.getValue() == null)) {
                tableNames.add(entry.getKey());
            }
        }
        return tableNames;
    }
}
