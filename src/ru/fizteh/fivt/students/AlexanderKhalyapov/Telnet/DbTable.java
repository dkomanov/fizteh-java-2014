package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

public final class DbTable implements Table, AutoCloseable {
    private TableProvider manager;
    private String name;
    private AtomicBoolean invalid = new AtomicBoolean(false);
    private Path tableDirectoryPath;
    private List<Class<?>> structure = new ArrayList<>();
    private Map<Long, TablePart> parts = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private ThreadLocal<Map<String, Storeable>> diff = ThreadLocal.withInitial(HashMap::new);

    public DbTable(TableProvider manager, Path tableDirectoryPath) throws IOException {
        if (manager == null || tableDirectoryPath == null) {
            throw new IllegalArgumentException("Unable to create table for"
                    + " null provider or/and null path to table directory");
        }
        this.manager = manager;
        this.tableDirectoryPath = tableDirectoryPath;
        this.name = tableDirectoryPath.getName(tableDirectoryPath.getNameCount() - 1).toString();
        try {
            readTableDir();
        } catch (DataBaseIOException e) {
            throw new DataBaseIOException("Error reading table '" + getName() + "': " + e.getMessage(), e);
        }
    }

    @Override
    public int commit() throws IOException {
        lock.writeLock().lock();
        try {
            checkTableIsNotRemoved();
            int savedChangesCounter = diff.get().size();
            for (Entry<String, Storeable> pair : diff.get().entrySet()) {
                TablePart part = parts.get(getHash(pair.getKey()));
                if (pair.getValue() == null) {
                    part.remove(pair.getKey());
                } else {
                    if (part == null) {
                        long hash = getHash(pair.getKey());
                        int dirNumber = Helper.unhashFirstIntFromLong(hash);
                        int fileNumber = Helper.unhashSecondIntFromLong(hash);
                        part = new TablePart(this, tableDirectoryPath, dirNumber, fileNumber);
                        parts.put(hash, part);
                    }
                    part.put(pair.getKey(), pair.getValue());
                }
            }
            diff.get().clear();
            writeTableToDir();
            return savedChangesCounter;
        } catch (IOException e) {
            throw new IOException("Error writing table '" + getName() + "' to its directory: " + e.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        checkTableIsNotRemoved();
        int rolledChangesCounter = diff.get().size();
        diff.get().clear();
        return rolledChangesCounter;
    }

    @Override
    public String getName() {
        checkTableIsNotRemoved();
        return name;
    }

    @Override
    public int getColumnsCount() {
        checkTableIsNotRemoved();
        return structure.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        checkTableIsNotRemoved();
        Serializer.checkIndexInBounds(structure.size(), columnIndex);
        return structure.get(columnIndex);
    }

    private long getHash(String key) {
        int dirNumber;
        int fileNumber;
        try {
            dirNumber = Math.abs(key.getBytes(Helper.ENCODING)[0] % Helper.NUMBER_OF_PARTITIONS);
            fileNumber = Math.abs((key.getBytes(Helper.ENCODING)[0] / Helper.NUMBER_OF_PARTITIONS)
                    % Helper.NUMBER_OF_PARTITIONS);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode key to " + Helper.ENCODING, e);
        }
        return Helper.hashIntPairAsLong(dirNumber, fileNumber);
    }

    private void checkTableIsNotRemoved() {
        if (invalid.get()) {
            throw new IllegalStateException("This table '" + name + "' has already removed");
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            checkTableIsNotRemoved();
            rollback();
            invalid.set(true);
            manager.removeTableFromList(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Storeable get(String key) {
        lock.readLock().lock();
        try {
            checkTableIsNotRemoved();
            if (key == null) {
                throw new IllegalArgumentException("Key is null");
            }
            Storeable value;
            if (diff.get().containsKey(key)) {
                value = diff.get().get(key);
            } else {
                TablePart part = parts.get(getHash(key));
                if (part == null) {
                    value = null;
                } else {
                    try {
                        value = part.get(key);
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException(e.getMessage(), e);
                    }
                }
            }
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        lock.readLock().lock();
        try {
            checkTableIsNotRemoved();
            if (key == null || value == null) {
                throw new IllegalArgumentException("Key or value is a null-string");
            }
            try {
                for (int i = 0; i < structure.size(); i++) {
                    if (value.getColumnAt(i) != null && structure.get(i) != value.getColumnAt(i).getClass()) {
                        throw new ColumnFormatException("Storeable has a wrong column format");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new ColumnFormatException("Storeable has a wrong " + "column format: " + e.getMessage(), e);
            }
            Storeable oldValue;
            if (!diff.get().containsKey(key)) {
                TablePart part = parts.get(getHash(key));
                if (part == null) {
                    oldValue = null;
                } else {
                    try {
                        oldValue = part.get(key);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            } else {
                oldValue = diff.get().remove(key);
            }
            diff.get().put(key, value);
            return oldValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Storeable remove(String key) {
        lock.readLock().lock();
        try {
            checkTableIsNotRemoved();
            if (key == null) {
                throw new IllegalArgumentException("Key is null");
            }
            Storeable removedValue;
            if (!diff.get().containsKey(key)) {
                TablePart part = parts.get(getHash(key));
                if (part == null) {
                    removedValue = null;
                } else {
                    try {
                        removedValue = part.get(key);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    diff.get().put(key, null);
                }
            } else {
                removedValue = diff.get().remove(key);
            }
            return removedValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            checkTableIsNotRemoved();
            int numberOfRecords = 0;
            for (Entry<Long, TablePart> part : parts.entrySet()) {
                numberOfRecords += part.getValue().getNumberOfRecords();
            }
            for (Entry<String, Storeable> pair : diff.get().entrySet()) {
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

    @Override
    public List<String> list() {
        lock.readLock().lock();
        try {
            checkTableIsNotRemoved();
            Set<String> keySet = new HashSet<>();
            for (Entry<Long, TablePart> pair : parts.entrySet()) {
                keySet.addAll(pair.getValue().list());
            }
            for (Entry<String, Storeable> pair : diff.get().entrySet()) {
                if (pair.getValue() == null) {
                    keySet.remove(pair.getKey());
                } else {
                    keySet.add(pair.getKey());
                }
            }
            List<String> list = new LinkedList<>();
            list.addAll(keySet);
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        checkTableIsNotRemoved();
        return diff.get().size();
    }

    TableProvider getManager() {
        checkTableIsNotRemoved();
        return manager;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + tableDirectoryPath.toAbsolutePath() + "]";
    }

    private void readTableDir() throws IOException {
        readSignature();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tableDirectoryPath)) {
            for (Path dir : stream) {
                Path dirPath = tableDirectoryPath.resolve(dir);
                if (!dir.getFileName().toString().matches(Helper.DIR_NAME_REGEX) || !dirPath.toFile().isDirectory()) {
                    if (dir.getFileName().toString().equals(Helper.SIGNATURE_FILE_NAME) && dirPath.toFile().isFile()) {
                        // Ignore signature file.
                        continue;
                    }
                    throw new DataBaseIOException(String.format("File '" + dir + "' is not a directory or "
                            + "doesn't match required name '[0-%1$d].dir'", Helper.NUMBER_OF_PARTITIONS - 1));
                }
                String[] fileList = dirPath.toFile().list();
                if (fileList.length == 0) {
                    throw new DataBaseIOException("Directory '" + dir + "' is empty.");
                }
                for (String file : fileList) {
                    Path filePath = dirPath.resolve(file);
                    if (!file.matches(Helper.FILE_NAME_REGEX) || !filePath.toFile().isFile()) {
                        throw new DataBaseIOException(String.format("File '" + file + "'" + " in directory '" + dir
                                        + "' is not a regular file or" + " doesn't match required name '[0-%1$d].dat'",
                                Helper.NUMBER_OF_PARTITIONS - 1));
                    }
                    int dirNumber = Integer.parseInt(dir.getFileName().toString()
                            .substring(0, dir.getFileName().toString().length() - 4));
                    int fileNumber = Integer.parseInt(file.substring(0, file.length() - 4));
                    TablePart part = new TablePart(this, tableDirectoryPath, dirNumber, fileNumber);
                    parts.put(Helper.hashIntPairAsLong(dirNumber, fileNumber), part);
                }
            }
        }
    }

    private void readSignature() throws DataBaseIOException {
        Path signatureFilePath = tableDirectoryPath.resolve(Helper.SIGNATURE_FILE_NAME);
        if (!signatureFilePath.toFile().isFile()) {
            throw new DataBaseIOException("Signature file '" + Helper.SIGNATURE_FILE_NAME + "' is missing");
        }
        try (Scanner scanner = new Scanner(signatureFilePath)) {
            String[] types = scanner.nextLine().split("\\s+");
            for (String typeName : types) {
                Class<?> typeClass = Helper.SUPPORTED_NAMES_TO_TYPES.get(typeName);
                if (typeClass == null) {
                    throw new IOException("file contains wrong type names");
                }
                structure.add(typeClass);
            }
        } catch (IOException | NoSuchElementException e) {
            throw new DataBaseIOException("Unable read signature from " + signatureFilePath.toString() + ": "
                    + e.getMessage(), e);
        }
    }

    private void writeTableToDir() throws DataBaseIOException {
        Iterator<Entry<Long, TablePart>> it = parts.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, TablePart> part = it.next();
            part.getValue().commit();
            if (part.getValue().getNumberOfRecords() == 0) {
                it.remove();
            }
        }
    }
}
