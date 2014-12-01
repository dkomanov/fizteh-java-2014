package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TypesParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.XmlOperations.XmlDeserializer;
import ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.XmlOperations.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDatabaseTableProvider implements TableProvider {

    static final String SIGNATURE_FILE = "signature.tsv";
    private static final int MAX_TABLES_NUMBER = 16;
    private String databaseDirectoryPath;
    private ThreadSafeDatabaseTable currentTable = null;
    HashMap<String, ThreadSafeDatabaseTable> tables =
            new HashMap<String, ThreadSafeDatabaseTable>();
    private final Lock tableLock = new ReentrantLock(true);

    public ThreadSafeDatabaseTableProvider(String directory) {
        if ((directory == null)
                || (directory.equals(""))) {
            throw new IllegalArgumentException(
                    "Directory's name can not be empty!");
        }

        this.databaseDirectoryPath = directory;
        File databaseDirectory = new File(directory);

        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException(
                    "Directory should not be a file!");
        }

        this.databaseDirectoryPath =
                databaseDirectory.getAbsolutePath();
        databaseDirectory = new File(databaseDirectoryPath);
        for (final File tableFile : databaseDirectory.listFiles()) {
            if (tableFile.isFile()) {
                continue;
            }

            List<Class<?>> columnTypes = readTableSignature(tableFile.getName());

            if (columnTypes == null) {
                throw new IllegalArgumentException("Table does not exist!");
            }

            ThreadSafeDatabaseTable table = new ThreadSafeDatabaseTable(
                    this, databaseDirectoryPath, tableFile.getName(), columnTypes);
            tables.put(table.getName(), table);
        }
    }

    @Override
    public List<String> getTableNames() {
        List<String> names = new ArrayList<String>();
        for (String name : tables.keySet()) {
            names.add(name);
        }
        return names;
    }

    @Override
    public Table getTable(String name) {
        tableLock.lock();
        try {
                if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Table's name cannot be empty");
            }

            checkTableName(name);

            ThreadSafeDatabaseTable table = tables.get(name);

            if (table == null) {
                return null;
            }

            if (currentTable != null && currentTable.getNumberOfUncommittedChanges() > 0) {
                throw new IllegalStateException(String.format("%d unsaved changes",
                        currentTable.getNumberOfUncommittedChanges()));
            }

            currentTable = table;

            return table;

        } finally {
            tableLock.unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        tableLock.lock();
        try {
                if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Table's name cannot be null!");
            }

            checkTableName(name);

            if (columnTypes == null || columnTypes.isEmpty()) {
                throw new IllegalArgumentException("Column types cannot be null!");
            }

            checkColumnTypes(columnTypes);

            if (tables.containsKey(name)) {
                return null;
            }

            ThreadSafeDatabaseTable table = new ThreadSafeDatabaseTable(this, databaseDirectoryPath, name, columnTypes);
            tables.put(name, table);
            return table;
        } finally {
            tableLock.unlock();
        }
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        XmlDeserializer deserializer = new XmlDeserializer(value);
        Storeable result = null;
        List<Object> values = new ArrayList<Object>(table.getColumnsCount());

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            try {
                Class<?> expectedType = table.getColumnType(index);
                Object columnValue = deserializer.getNext(expectedType);
                values.add(columnValue);
            } catch (ColumnFormatException e) {
                throw new ParseException("Incompatible type: " + e.getMessage(), index);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Xml representation doesn't match the !", index);
            }
        }

        try {
            deserializer.close();
            result = createFor(table, values);
        } catch (ColumnFormatException e) {
            throw new ParseException("Incompatible types: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Xml representation doesn't match the format!", 0);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), 0);
        }

        return result;
    }

    @Override
    public String serialize(Table table, Storeable value)
            throws ColumnFormatException {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null!");
        }

        try {
            XmlSerializer xmlSerializer = new XmlSerializer();
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(value.getColumnAt(index));
            }
            xmlSerializer.close();
            return xmlSerializer.getRepresentation();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Incorrect value!");
        }
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return createRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        if (values == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Values list cannot be empty!");
        }

        DatabaseTableRow row = createRow(table);
        row.setColumns(values);

        return row;
    }

    private DatabaseTableRow createRow(Table table) {
        DatabaseTableRow row = new DatabaseTableRow();

        for (int index = 0; index < table.getColumnsCount(); ++index) {
            row.addColumn(table.getColumnType(index));
        }

        return row;
    }

    public static void deleteFile(File fileToDelete) {
        if (!fileToDelete.exists()) {
            return;
        }

        if (fileToDelete.isDirectory()) {
            for (final File file : fileToDelete.listFiles()) {
                deleteFile(file);
            }
        }

        fileToDelete.delete();
    }

    public void removeTable(String name) {
        tableLock.lock();
        try {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException(
                        this.getClass().toString() + ": Table's name cannot be empty!");
            }

            if (!tables.containsKey(name)) {
                throw new IllegalStateException(
                        String.format("%s not exists", name));
            }

            tables.remove(name);

            File tableFile = new File(databaseDirectoryPath, name);
            deleteFile(tableFile);
        } finally {
            tableLock.unlock();
        }
    }

    public void showTables() {
        for (final Map.Entry<String, ThreadSafeDatabaseTable> map: tables.entrySet()) {
            System.out.println(map.getKey() + ' ' + map.getValue().size());
        }
    }

    public final void exit() throws IOException {
        if (currentTable != null) {
            currentTable.save();
        }
    }

    private List<Class<?>> readTableSignature(String tableName) {
        File tableDirectory = new File(databaseDirectoryPath, tableName);
        File signatureFile = new File(tableDirectory, SIGNATURE_FILE);
        String signature = null;
        if (!signatureFile.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(signatureFile));
            signature = reader.readLine();
        } catch (IOException e) {
            System.err.println(this.getClass().toString() + ": Error loading signature file: " + e.getMessage());
            return null;
        }

        if (signature == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Incorrect signature file!");
        }

        List<Class<?>> columnTypes = new ArrayList<Class<?>>();
        for (final String columnType : signature.split("\\s+")) {
            Class<?> type = TypesParser.getTypeByName(columnType);
            if (type == null) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Unknown type!");
            }
            columnTypes.add(type);
        }
        return columnTypes;
    }

    private void checkColumnTypes(List<Class<?>> columnTypes) {
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException(this.getClass().toString() + ": Unknown column type!");
            }
            TypesParser.getNameByType(columnType);
        }
    }

    private void checkTableName(String name) {
        if (!name.matches("[0-9A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Bad symbol!");
        }
    }

    public void clear() {
        tables.clear();
    }
}
