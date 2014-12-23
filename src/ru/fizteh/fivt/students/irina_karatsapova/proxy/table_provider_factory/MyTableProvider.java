package ru.fizteh.fivt.students.irina_karatsapova.proxy.table_provider_factory;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.exceptions.ThreadInterruptException;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.TypeTransformer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.Utils;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.XmlDeserializer;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.utils.XmlSerializer;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyTableProvider implements TableProvider {
    private static final String SIGNATURE_FILENAME = "signature.tsv";

    boolean loaded = false;
    private File databaseFile;
    private Map<String, Table> tables = new HashMap<>();
    private ReentrantReadWriteLock databaseAccessLock = new ReentrantReadWriteLock(true);

    MyTableProvider(String dir) throws ThreadInterruptException {
        loaded = true;
        databaseFile = Utils.toFile(dir);
        load();
    }

    private void load() throws ThreadInterruptException {
        for (File file : databaseFile.listFiles()) {
            String tableName = file.getName();
            Path tablePath = Utils.makePathAbsolute(databaseFile, tableName);
            MyTable table = new MyTable(tablePath.toString(), this);
            tables.put(tableName, table);
        }
    }

    public List<String> getTableNames() {
        checkLoaded();
        databaseAccessLock.readLock().lock();
        List<String> tableNamesList;
        try {
            Set<String> tableNamesSet = tables.keySet();
            tableNamesList = new ArrayList<>();
            tableNamesList.addAll(tableNamesSet);
        } finally {
            databaseAccessLock.readLock().unlock();
        }
        return tableNamesList;
    }

    public Table getTable(String name) {
        checkLoaded();
        Utils.checkNotNull(name);
        MyTable lookedForTable = null;
        databaseAccessLock.readLock().lock();
        try {
            if (tables.containsKey(name)) {
                lookedForTable = (MyTable) tables.get(name);
            }
        } finally {
            databaseAccessLock.readLock().unlock();
        }
        if (lookedForTable != null && !lookedForTable.loaded) {
            databaseAccessLock.writeLock().lock();
            try {
                lookedForTable = new MyTable(lookedForTable);
                tables.remove(name);
                tables.put(name, lookedForTable);
                lookedForTable.renewDiff();
            } finally {
                databaseAccessLock.writeLock().unlock();
            }
        }
        return lookedForTable;
    }

    public Table createTable(String name, List<Class<?>> columnTypes) throws ThreadInterruptException {
        checkLoaded();
        Utils.checkNotNull(name);
        Utils.checkNotNull(columnTypes);
        MyTable createdTable = null;
        databaseAccessLock.writeLock().lock();
        try {
            if (!tables.containsKey(name)) {
                File tableFile = checkAndCreateTableFile(name);
                createSignatureFile(tableFile, columnTypes);
                createdTable = new MyTable(tableFile.toString(), this);
                tables.put(name, createdTable);
            }
        } finally {
            databaseAccessLock.writeLock().unlock();
        }
        return createdTable;
    }

    public void removeTable(String name) throws ThreadInterruptException {
        checkLoaded();
        Utils.checkNotNull(name);
        databaseAccessLock.writeLock().lock();
        try {
            if (!tables.containsKey(name)) {
                throw new IllegalStateException("The table does not exist");
            } else {
                File tablePath = Paths.get(databaseFile.toString(), name).toFile();
                Utils.rmdirs(tablePath);
                tables.remove(name);
            }
        } finally {
            databaseAccessLock.writeLock().unlock();
        }
    }

    public Storeable deserialize(Table table, String stringValue) throws ParseException {
        checkLoaded();
        Utils.checkNotNull(stringValue);
        List<Object> values = new ArrayList<>();
        try {
            XmlDeserializer xmlDeserializer = new XmlDeserializer(stringValue);
            xmlDeserializer.start();
            for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
                Class expectedType = table.getColumnType(columnIndex);
                Object value;
                try {
                    value = xmlDeserializer.getNext(expectedType);
                } catch (ColumnFormatException e) {
                    throw new ColumnFormatException(e.getMessage() + " in column " + columnIndex);
                }
                values.add(value);
            }
            xmlDeserializer.finish();
        } catch (ParseException e) {
            throw new ParseException("Incorrect xml representation: " + e.getMessage(), 0);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException(e.getMessage()
                                                + ", there should be " + table.getColumnsCount() + " columns");
        }
        return createFor(table, values);
    }

    public String serialize(Table table, Storeable tableRawValue)
                            throws ColumnFormatException, IndexOutOfBoundsException, ThreadInterruptException {
        checkLoaded();
        Utils.checkNotNull(tableRawValue);
        try {
            XmlSerializer xmlSerializer = new XmlSerializer();
            xmlSerializer.start();
            TableUtils.checkColumnsFormat(table, tableRawValue);
            for (int index = 0; index < table.getColumnsCount(); ++index) {
                xmlSerializer.write(tableRawValue.getColumnAt(index));
            }
            xmlSerializer.finish();
            return xmlSerializer.getResult();
        } catch (IOException e) {
            throw new ThreadInterruptException("Serialization: " + e.getMessage());
        }
    }

    public Storeable createFor(Table table) {
        checkLoaded();
        List<Class<?>> types = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            types.add(table.getColumnType(columnIndex));
        }
        return new MyTableRaw(types);
    }

    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkLoaded();
        if (values.size() != table.getColumnsCount()) {
            throw new IndexOutOfBoundsException("wrong number of values in the list: " + values.size()
                    + " (table contains " + table.getColumnsCount() + "columns)");
        }
        Storeable tableRaw = createFor(table);
        for (int columnIndex = 0; columnIndex < table.getColumnsCount(); ++columnIndex) {
            tableRaw.setColumnAt(columnIndex, values.get(columnIndex));
        }
        return tableRaw;
    }

    public String toString() {
        checkLoaded();
        return getClass().getSimpleName() + "[" + databaseFile + "]";
    }

    public void close() {
        checkLoaded();
        databaseAccessLock.writeLock().lock();
        try {
            for (Table table: tables.values()) {
                table.close();
            }
            loaded = false;
        } finally {
            databaseAccessLock.writeLock().unlock();
        }
    }



    private File checkAndCreateTableFile(String name) throws ThreadInterruptException {
        File file;
        try {
            file = Utils.makePathAbsolute(databaseFile, name).toFile();
        } catch (InvalidPathException e) {
            throw new ThreadInterruptException("init: Incorrect name");
        }
        if (!file.getName().equals(name)) {
            throw new ThreadInterruptException("init: Incorrect name");
        }
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw new ThreadInterruptException("init: Incorrect name");
            }
        }
        if (!file.isDirectory()) {
            throw new ThreadInterruptException("init: Incorrect name: Main directory is a file");
        }
        return file;
    }

    private void createSignatureFile(File tableFile, List<Class<?>> columnTypes) {
        File signatureFile = Utils.makePathAbsolute(tableFile, SIGNATURE_FILENAME).toFile();
        try {
            signatureFile.createNewFile();
        } catch (IOException e) {
            throw new ThreadInterruptException("Can't create signature file");
        }
        try (PrintWriter bufWriter = new PrintWriter(new BufferedWriter(new FileWriter(signatureFile)))) {
            List<String> typeNames = new ArrayList<>();
            for (Class<?> type: columnTypes) {
                String typeName = TypeTransformer.getStringByType(type);
                typeNames.add(typeName);
            }
            bufWriter.print(String.join(" ", typeNames));
            bufWriter.flush();
        } catch (FileNotFoundException e) {
            throw new ThreadInterruptException("That is strange, but signature file does not exist");
        } catch (IOException e) {
            throw new ThreadInterruptException("Can't write into the signature file");
        }
    }

    private void checkLoaded() throws IllegalStateException {
        if (!loaded) {
            throw new IllegalStateException("Provider is not loaded");
        }
    }
}
