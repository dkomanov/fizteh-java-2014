package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.FileMap.TableState;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TypesParser;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.XmlOperations.XmlDeserializer;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.XmlOperations.XmlSerializer;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDatabaseTableProvider implements TableProvider, AutoCloseable, RemoteTableProvider {

    static final String SIGNATURE_FILE = "signature.tsv";
    private String databaseDirectoryPath;
    private ThreadSafeDatabaseTable currentTable = null;
    HashMap<String, ThreadSafeDatabaseTable> tables =
            new HashMap<String, ThreadSafeDatabaseTable>();
    private final Lock tableLock = new ReentrantLock(true);
    protected TableState state;

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
        state = TableState.WORKING;
    }

    @Override
    public List<String> getTableNames() {
        state.checkOperationCorrect();
        List<String> names = new ArrayList<String>();
        for (String name : tables.keySet()) {
            names.add(name);
        }
        return names;
    }

    @Override
    public Table getTable(String name) {
        state.checkOperationCorrect();
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
        state.checkOperationCorrect();
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
        state.checkOperationCorrect();
        return createRow(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values)
            throws ColumnFormatException, IndexOutOfBoundsException {
        state.checkOperationCorrect();
        if (values == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Values list cannot be empty!");
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
                        this.getClass().getSimpleName() + ": Table's name cannot be empty!");
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
        System.out.println(tables.size());
        for (final Map.Entry<String, ThreadSafeDatabaseTable> map: tables.entrySet()) {
            System.out.println(map.getKey() + ' ' + map.getValue().size());
        }
    }

    public void exit() throws IOException {
        if (currentTable != null) {
            currentTable.save();
        }
    }

    private List<Class<?>> readTableSignature(String tableName) {
        File tableDirectory = new File(databaseDirectoryPath, tableName);
        File signatureFile = new File(tableDirectory, SIGNATURE_FILE);
        String signature;
        if (!signatureFile.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(signatureFile));
            signature = reader.readLine();
        } catch (IOException e) {
            System.err.println(this.getClass().getSimpleName() + ": Error loading signature file: " + e.getMessage());
            return null;
        }

        if (signature == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Incorrect signature file!");
        }

        List<Class<?>> columnTypes = new ArrayList<>();
        for (final String columnType : signature.split("\\s+")) {
            Class<?> type = TypesParser.getTypeByName(columnType);
            if (type == null) {
                throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Unknown type!");
            }
            columnTypes.add(type);
        }
        return columnTypes;
    }

    private void checkColumnTypes(List<Class<?>> columnTypes) {
        for (final Class<?> columnType : columnTypes) {
            if (columnType == null) {
                throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Unknown column type!");
            }
            TypesParser.getNameByType(columnType);
        }
    }

    private void checkTableName(String name) {
        if (!name.matches("[0-9A-Za-zА-Яа-я]+")) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Bad symbol!");
        }
    }

    public void clear() {
        tables.clear();
    }

    @Override
    public void close() {
        if (state.equals(TableState.CLOSED)) {
            return;
        }
        for (final String tableName : tables.keySet()) {
            tables.get(tableName).close();
        }
        state = TableState.CLOSED;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass().getSimpleName(), databaseDirectoryPath);
    }
}
