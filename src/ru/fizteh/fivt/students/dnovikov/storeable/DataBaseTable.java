package ru.fizteh.fivt.students.dnovikov.storeable;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.LoadOrSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataBaseTable implements Table {
    private static final int FILES_COUNT = 16;
    private static final int FOLDERS_COUNT = 16;
    private String tableName;
    private List<SingleTable> tableParts;
    private SingleTable[][] parts;
    private DataBaseProvider databaseConnector;
    private ThreadLocal<Map<String, Storeable>> diffs = ThreadLocal.withInitial(HashMap::new);
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ArrayList<Class<?>> types;

    public DataBaseTable(String name, DataBaseProvider dbConnector) throws LoadOrSaveException {
        tableName = name;
        tableParts = new ArrayList<>();
        databaseConnector = dbConnector;
        readSignature();
        parts = new SingleTable[FOLDERS_COUNT][];
        for (int i = 0; i < FOLDERS_COUNT; ++i) {
            parts[i] = new SingleTable[FILES_COUNT];
        }
        for (int i = 0; i < FOLDERS_COUNT; ++i) {
            for (int j = 0; j < FILES_COUNT; ++j) {
                parts[i][j] = new SingleTable(i, j, this);
                tableParts.add(parts[i][j]);
            }
        }
    }

    private void readSignature() throws LoadOrSaveException {
        types = new ArrayList<>();
        Path signatureFilePath = getTableDirectory().resolve(Utils.SIGNATURE_FILE_NAME);
        if (!signatureFilePath.toFile().isFile()) {
            throw new LoadOrSaveException("cannot find signature file: " + Utils.SIGNATURE_FILE_NAME);
        }
        try (Scanner scanner = new Scanner(signatureFilePath)) {
            String[] signatures = scanner.nextLine().split("\\s+");
            for (String typeName : signatures) {
                Class<?> typeClass = Utils.SUPPORTED_NAMES_TO_TYPES.get(typeName);
                if (typeClass == null) {
                    throw new IOException("file contains wrong type names");
                }
                types.add(typeClass);
            }
        } catch (IOException | NoSuchElementException e) {
            throw new LoadOrSaveException("canont read signature from "
                    + signatureFilePath.toString() + ": " + e.getMessage());
        }
    }

    public DataBaseProvider getDatabaseConnector() {
        return databaseConnector;
    }

    private SingleTable selectSingleTable(String key) {
        int hashCode = key.hashCode();
        int directoryNumber = hashCode % FOLDERS_COUNT;
        int fileNumber = hashCode / FOLDERS_COUNT % FILES_COUNT;
        if (directoryNumber < 0) {
            directoryNumber += FOLDERS_COUNT;
        }
        if (fileNumber < 0) {
            fileNumber += FILES_COUNT;
        }
        return parts[directoryNumber][fileNumber];
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        try {
            List<String> result = new ArrayList<>();
            Set<String> setResult = new HashSet<>();
            for (SingleTable table : tableParts) {
                List<String> keys = table.list();
                setResult.addAll(keys);
            }
            for (Entry<String, Storeable> pair : diffs.get().entrySet()) {
                if (pair.getValue() == null) {
                    setResult.remove(pair.getKey());
                } else {
                    setResult.add(pair.getKey());
                }
            }
            result.addAll(setResult);
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        lock.readLock().lock();
        try {
            if (key == null) {
                throw new IllegalArgumentException("error of get: key is null");
            }
            Storeable value;
            SingleTable table = selectSingleTable(key);
            if (diffs.get().containsKey(key)) {
                value = diffs.get().get(key);
            } else {
                value = table.get(key);
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Path getTableDirectory() {
        return databaseConnector.getRootDirectory().resolve(tableName);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("error of put: key or value is null");
        }
        lock.readLock().lock();
        try {
            try {
                for (int i = 0; i < types.size(); i++) {
                    if (value.getColumnAt(i) != null && types.get(i) != value.getColumnAt(i).getClass()) {
                        throw new ColumnFormatException("value: wrong column format");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("value: wrong column format" + e.getMessage(), e);
            }
            SingleTable table = selectSingleTable(key);
            Storeable oldValue;
            if (!diffs.get().containsKey(key)) {
                oldValue = table.get(key);
                String stringValue = databaseConnector.serialize(this, value);
                String stringOldValue = databaseConnector.serialize(this, oldValue);
                if (oldValue != null && stringOldValue.equals(stringValue)) {
                    return oldValue;
                }
            } else {
                oldValue = diffs.get().remove(key);
            }
            diffs.get().put(key, value);
            return oldValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error of remove: key is null");
        }
        lock.readLock().lock();
        try {
            SingleTable table = selectSingleTable(key);
            Storeable deleted;
            if (!diffs.get().containsKey(key)) {
                deleted = table.get(key);
                diffs.get().put(key, null);
            } else {
                deleted = diffs.get().remove(key);
                if (table.get(key) != null) {
                    diffs.get().put(key, null);
                }
            }
            return deleted;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void drop() throws LoadOrSaveException {
        File tableDirectory = getTableDirectory().toFile();
        for (SingleTable[] directory : parts) {
            for (SingleTable singleTableFile : directory) {
                singleTableFile.drop();
            }
        }
        File signaturePath = getTableDirectory().resolve(Utils.SIGNATURE_FILE_NAME).toFile();
        signaturePath.delete();
        tableDirectory.delete();
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return list().size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int commit() throws LoadOrSaveException {
        lock.writeLock().lock();
        try {
            int changesCount = diffs.get().size();
            for (Entry<String, Storeable> entryDiff : diffs.get().entrySet()) {
                SingleTable table = selectSingleTable(entryDiff.getKey());
                if (entryDiff.getValue() == null) {
                    table.remove(entryDiff.getKey());
                } else {
                    table.put(entryDiff.getKey(), entryDiff.getValue());
                }
            }
            diffs.get().clear();
            save();
            return changesCount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        int changesCount = diffs.get().size();
        diffs.get().clear();
        return changesCount;
    }


    @Override
    public int getColumnsCount() {
        return types.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return types.get(columnIndex);
    }

    public void save() throws LoadOrSaveException {
        for (SingleTable singleTable : tableParts) {
            singleTable.save();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return diffs.get().size();
    }
}



