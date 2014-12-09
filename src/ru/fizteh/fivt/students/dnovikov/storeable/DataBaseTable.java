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

public class DataBaseTable implements Table {
    private static final int FILES_COUNT = 16;
    private static final int FOLDERS_COUNT = 16;
    private String tableName;
    private List<SingleTable> tableParts;
    private SingleTable[][] parts;
    private DataBaseProvider databaseConnector;
    private Map<String, Storeable> diffs;
    private ArrayList<Class<?>> types;

    public DataBaseTable(String name, DataBaseProvider dbConnector) throws LoadOrSaveException {
        diffs = new HashMap<>();
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
        List<String> result = new ArrayList<>();
        Set<String> setResult = new HashSet<>();
        for (SingleTable table : tableParts) {
            List<String> keys = table.list();
            setResult.addAll(keys);
        }
        for (Entry<String, Storeable> pair : diffs.entrySet()) {
            if (pair.getValue() == null) {
                setResult.remove(pair.getKey());
            } else {
                setResult.add(pair.getKey());
            }
        }
        result.addAll(setResult);
        return result;
    }

    @Override
    public String getName() {
        return tableName;
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error of get: key is null");
        }
        Storeable value;
        SingleTable table = selectSingleTable(key);
        if (diffs.containsKey(key)) {
            value = diffs.get(key);
        } else {
            value = table.get(key);
        }
        return value;
    }

    public Path getTableDirectory() {
        return databaseConnector.getRootDirectory().resolve(tableName);
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("error of put: key or value is null");
        }
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
        if (!diffs.containsKey(key)) {
            oldValue = table.get(key);
            String stringValue = databaseConnector.serialize(this, value);
            String stringOldValue = databaseConnector.serialize(this, oldValue);
            if (oldValue != null && stringOldValue.equals(stringValue)) {
                return oldValue;
            }
        } else {
            oldValue = diffs.remove(key);
        }
        diffs.put(key, value);
        return oldValue;
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("error of remove: key is null");
        }
        SingleTable table = selectSingleTable(key);

        Storeable deleted;
        if (!diffs.containsKey(key)) {
            deleted = table.get(key);
            diffs.put(key, null);
        } else {
            deleted = diffs.remove(key);
            if (table.get(key) != null) {
                diffs.put(key, null);
            }
        }
        return deleted;
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
        return list().size();
    }

    @Override
    public int commit() throws LoadOrSaveException {
        int changesCount = diffs.size();
        for (Entry<String, Storeable> entryDiff : diffs.entrySet()) {
            SingleTable table = selectSingleTable(entryDiff.getKey());
            if (entryDiff.getValue() == null) {
                table.remove(entryDiff.getKey());
            } else {
                table.put(entryDiff.getKey(), entryDiff.getValue());
            }
        }
        diffs.clear();
        save();
        return changesCount;
    }

    @Override
    public int rollback() {
        int changesCount = diffs.size();
        diffs.clear();
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
        return diffs.size();
    }
}



