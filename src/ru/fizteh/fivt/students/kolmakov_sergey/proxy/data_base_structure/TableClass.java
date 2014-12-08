package ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_structure;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.CastMaker;
import ru.fizteh.fivt.students.kolmakov_sergey.proxy.util.Coordinates;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TableClass implements Table, AutoCloseable {

    private final String name;
    private final Path tablePath;
    private final Map<Coordinates, DataFile> tableMap;
    private ThreadLocal<Map<String, Storeable>> difference; // (null in Value) -> this entry must be removed.
    private final String unexpectedFilesMessage = "Unexpected files found in directory ";
    private final String emptyFoldersMessage = "Empty folders found";
    private final String signatureFileName = "signature.tsv";
    private final TableProvider tableProvider;
    private AtomicBoolean wasRemoved = new AtomicBoolean(false);
    private final ReadWriteLock lock;
    private List<Class<?>> columnTypes;

    public TableClass(Path tablePath, String name, TableProvider tableProvider, List<Class<?>> columnTypes)
            throws DatabaseCorruptedException {
        this.tableProvider = tableProvider;
        tableMap = new HashMap<>();
        difference = ThreadLocal.withInitial(() -> new HashMap<>());
        this.tablePath = tablePath;
        this.name = name;
        lock = new ReentrantReadWriteLock(true);

        if (columnTypes == null) {
            readColumnTypes();
        } else { // Write columnTypes to the table directory.
            this.columnTypes = new ArrayList<>(columnTypes);
            Path currentFolderPath = this.tablePath.resolve(signatureFileName);
            File currentFile = currentFolderPath.toFile();
            try {
                currentFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error while creating table: " + e.getMessage());
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(currentFolderPath.toString()))) {
                for (Class<?> currentClass: columnTypes) {
                    writer.print(CastMaker.classToString(currentClass));
                    writer.print(' ');
                }
            } catch (IOException e) {
                throw new RuntimeException("Can't write columnTypes to table directory: " + e.getMessage());
            }
        }
        // Reading keys and values.
        String[] folders = this.tablePath.toFile().list();
        for (String currentFolderName : folders) {
            if (currentFolderName.endsWith(signatureFileName)) {
                continue;
            }
            Path currentFolderPath = this.tablePath.resolve(currentFolderName);
            if (!currentFolderName.matches(TableManager.FOLDER_NAME_PATTERN)
                    || !currentFolderPath.toFile().isDirectory()) {
                throw new DatabaseCorruptedException(unexpectedFilesMessage + tablePath.toString());
            }
            String[] fileList = currentFolderPath.toFile().list();
            if (fileList.length == 0) {
                throw new DatabaseCorruptedException(tablePath.toString() + " : " + emptyFoldersMessage);
            }
            for (String currentFileName : fileList) {
                Path filePath = currentFolderPath.resolve(currentFileName);
                if (!currentFileName.matches(TableManager.FILE_NAME_PATTERN) || !filePath.toFile().isFile()) {
                    throw new DatabaseCorruptedException(unexpectedFilesMessage + tablePath.toString());
                }
                int folderIndex = TableManager.extractFolderNumber(currentFolderName);
                int fileIndex = TableManager.extractDataFileNumber(currentFileName);
                try {
                    DataFile currentDataFile;
                    currentDataFile = new DataFile(this.tablePath,
                            new Coordinates(folderIndex, fileIndex), this, tableProvider);
                    tableMap.put(new Coordinates(folderIndex, fileIndex), currentDataFile);
                } catch (IOException e) {
                    throw new DatabaseCorruptedException(this.tablePath.resolve(
                            Paths.get(Integer.toString(folderIndex) + ".dir",
                                    Integer.toString(fileIndex) + ".dat")).toString() + " corrupted");
                }
            }
        }
    }

    private void readColumnTypes() throws DatabaseCorruptedException {
        columnTypes = new ArrayList<>();
        Path currentFolderPath = this.tablePath.resolve(signatureFileName);
        List<String> list;
        try (Scanner in = new Scanner(new File(currentFolderPath.toString()))) {
            list = Arrays.asList(in.nextLine().split("\\s+"));
            for (String currentString : list) {
                columnTypes.add(CastMaker.stringToClass(currentString));
            }
        } catch (FileNotFoundException e) {
            throw new DatabaseCorruptedException("Can't find " + signatureFileName + " in table " + name);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new DatabaseCorruptedException(signatureFileName + " is corrupted in table " + name);
        }
    }

    @Override
    public int commit() {
        lock.writeLock().lock();
        int numberOfChanges = difference.get().size();
        try { // Apply difference to tableMap.
            checkActuality();
            for (Entry<String, Storeable> currentOperation : difference.get().entrySet()) {
                DataFile dataFile = tableMap.get(new Coordinates(currentOperation.getKey(),
                        TableManager.NUMBER_OF_PARTITIONS));
                if (currentOperation.getValue() == null) {
                    dataFile.remove(currentOperation.getKey()); // In this case dataFile can't be null.
                } else {
                    try {
                        if (dataFile == null) {
                            dataFile = new DataFile(tablePath, new Coordinates(currentOperation.getKey(),
                                    TableManager.NUMBER_OF_PARTITIONS), this, tableProvider);
                            tableMap.put(new Coordinates(currentOperation.getKey(),
                                    TableManager.NUMBER_OF_PARTITIONS), dataFile);
                        }
                        dataFile.put(currentOperation.getKey(), currentOperation.getValue());
                    } catch (DatabaseCorruptedException e) {
                        throw new RuntimeException("Can't commit table '" + getName()
                                + "': " + e.getMessage(), e);
                    }
                }
            }
            difference.get().clear();
            // Now write to disk.
            List<Coordinates> mustBeRemoved = new LinkedList<>();
            for (Entry<Coordinates, DataFile> currentEntry : tableMap.entrySet()) {
                currentEntry.getValue().commit();
                if (currentEntry.getValue().size() == 0) {
                    mustBeRemoved.add(currentEntry.getKey());
                }
            }
            mustBeRemoved.forEach(tableMap::remove);
        } catch (IOException e) {
            throw new RuntimeException("Can't commit table '" + name + "': " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
        return numberOfChanges;
    }

    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: null key");
        }
        lock.readLock().lock();
        try {
            checkActuality();
            Storeable value;
            if (difference.get().containsKey(key)) {
                value = difference.get().get(key);
            } else {
                DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
                if (expectedDataFile == null) {
                    value = null;
                } else {
                    value = expectedDataFile.get(key);
                }
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: null arguments");
        }
        lock.readLock().lock();
        try {
            checkActuality();
            Storeable oldValue;
            if (!difference.get().containsKey(key)) {
                DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
                if (expectedDataFile == null) {
                    oldValue = null;
                } else {
                    oldValue = expectedDataFile.get(key);
                }
            } else {
                oldValue = difference.get().get(key);
            }
            difference.get().put(key, value);
            return oldValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key == null");
        }
        lock.readLock().lock();
        try {
            checkActuality();
            Storeable removedValue;
            if (!difference.get().containsKey(key)) {
                DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
                if (expectedDataFile == null) {
                    removedValue = null;
                } else {
                    removedValue = expectedDataFile.get(key);
                    difference.get().put(key, null);
                }
            } else {
                removedValue = difference.get().remove(key);
            }
            return removedValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        lock.readLock().lock();
        try {
            checkActuality();
            Set<String> mergeResult = new HashSet<>();
            for (DataFile dataFile : tableMap.values()) {
                mergeResult.addAll(dataFile.list());
            }
            for (Entry<String, Storeable> currentOperation : difference.get().entrySet()) {
                if (currentOperation.getValue() == null) {
                    mergeResult.remove(currentOperation.getKey());
                } else {
                    mergeResult.add(currentOperation.getKey());
                }
            }
            List<String> list = new LinkedList<>();
            list.addAll(mergeResult);
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int rollback() {
        lock.readLock().lock();
        try {
            checkActuality();
            int numberOfChanges = difference.get().size();
            difference.get().clear();
            return numberOfChanges;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        lock.readLock().lock();
        try {
            checkActuality();
            return difference.get().size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getColumnsCount() {
        lock.readLock().lock();
        try {
            checkActuality();
            return columnTypes.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        lock.readLock().lock();
        try {
            checkActuality();
            try {
                return columnTypes.get(columnIndex);
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("Column with index " + columnIndex + " doesn't exist in this table");
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getName() {
        lock.readLock().lock();
        try {
            checkActuality();
            return name;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkActuality();
            int numberOfRecords = 0;
            for (DataFile currentTable : tableMap.values()) {
                numberOfRecords += currentTable.size();
            }
            for (Entry<String, Storeable> pair : difference.get().entrySet()) {
                if (pair.getValue() == null) {
                    numberOfRecords--;
                } else {
                    numberOfRecords++;
                }
            }
            return numberOfRecords;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkActuality() {
        if (wasRemoved.get()) {
            throw new IllegalStateException("Table '" + name + "' does not exist already.");
        }
    }

    public void setRemovedFlag() {
        lock.writeLock().lock();
        wasRemoved.set(true);
        lock.writeLock().unlock();
    }

    @Override
    public void close() throws Exception {
        rollback();
        if (!wasRemoved.getAndSet(true)) {
            ((TableManager) tableProvider).replaceClosedTableWithNew(name, this);
        } else {
            throw new IllegalStateException("Table '" + name + "' was closed already.");
        }
    }

    public Table makeNewTableFromClosed() throws DatabaseCorruptedException {
        return new TableClass(tablePath, name, tableProvider, columnTypes);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tablePath.toAbsolutePath().toString() + "]";
    }
}
