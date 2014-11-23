package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Represents the data table containing pairs key-value. The keys must be unique.
 * Transactionality: changes are committed or rolled back using the methods {link #commit ()} or {link #rollback ()}.
 * It is assumed that among calls of these methods there aren't any I/O operations.
 * This interface is not thread safe.
 */
public final class DbTable implements Table {
    private TableProvider provider;
    private String name;
    private boolean invalid;
    private Path tableDirectoryPath;
    private List<Class<?>> structure;
    private Map<Long, TablePart> parts;
    private Map<String, Storeable> diff;
    
    /**
     * @param provider Link to TableProvider, which created the table. 
     * @param tableDirectoryPath Path to table directory.
     * @throws DataBaseIOException If table directory is corrupted.
     * @throws IllegalArgumentException If 
     */
    public DbTable(TableProvider provider, Path tableDirectoryPath)
            throws DataBaseIOException {
        if (provider == null || tableDirectoryPath == null) {
            throw new IllegalArgumentException("Unable to create table for"
                    + " null provider or/and null path to table directory");
        }
        parts = new HashMap<>();
        diff = new HashMap<>();
        structure = new ArrayList<>();
        this.provider = provider;
        this.tableDirectoryPath = tableDirectoryPath;
        this.name = tableDirectoryPath.getName(tableDirectoryPath.getNameCount() - 1).toString();
        invalid = false;
        try {
            readTableDir();
        } catch (DataBaseIOException e) {
            throw new DataBaseIOException("Error reading table '" + getName()
                    + "': " + e.getMessage(), e);
        }
    }
    
    /**
     * Commit the changes.
     * @return Number of committed changes.
     * @throws IOException If an I/O error occurred. The integrity of the table can not be guaranteed.
     */
    @Override
    public int commit() throws IOException {
        checkTableIsNotRemoved();
        int savedChangesCounter = diff.size();
        try {
            for (Entry<String, Storeable> pair : diff.entrySet()) {
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
            diff.clear();
            writeTableToDir();
        } catch (IOException e) {
            throw new IOException("Error writing table '" + getName()
                    + "' to its directory: " + e.getMessage(), e);
        }
        return savedChangesCounter;
    }
    
    /** 
     * Rolls back the changes since the last commit.
     * @return Number of rolled back changes.
     */
    @Override
    public int rollback() {
        checkTableIsNotRemoved();
        int rolledChangesCounter = diff.size();
        diff.clear();
        return rolledChangesCounter;
    }
    
    /**
     * @return Name of the table.
     */
    @Override
    public String getName() {
        checkTableIsNotRemoved();
        return name;
    }
    
    /**
     * @return Number of columns in table. 
     */
    @Override
    public int getColumnsCount() {
        checkTableIsNotRemoved();
        return structure.size();
    }

    /**
     * @param columnIndex Index of column. Starts from zero.
     * @return Class representing type of value.
     * @throws IndexOutOfBoundsException Wrong index of column.
     */
    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        checkTableIsNotRemoved();
        if (columnIndex < 0 || columnIndex >= structure.size()) {
            throw new IndexOutOfBoundsException("Column index out of bounds: "
                    + "expected index from 0 to " + structure.size()
                    + ", but found " + columnIndex);
        }
        return structure.get(columnIndex);
    }
    
    /**
     * Returns hashed file and directory numbers where the key should be placed.
     * @param key Key.
     * @return Long hash of file number and its directory number.
     */
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
    
    /**
     * Checks if object doesn't refer to removed table.
     * @throws IllegalStateException If table has already removed.
     */
    private void checkTableIsNotRemoved() {
        if (invalid) {
            throw new IllegalStateException("This table '" + name + "' has already removed");
        }
    }
    
    /**
     * Set this DbTable object to removed state.
     */
    public void invalidate() {
        invalid = true;
    }
    
    /**
     * Gets the value of the specified key.
     * @param key The key is for searching the value. Can not be null.
     * For indexes on non-string fields parameter is a serialized column value.
     * It is required to parse.
     * @return Value. If not found, returns null.
     * @throws IllegalArgumentException If {@link TablePart#get(String)
     * TablePart.get} method fails.
     */
    @Override
    public Storeable get(String key) {
        checkTableIsNotRemoved();
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        Storeable value;
        if (diff.containsKey(key)) {
            value = diff.get(key);
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
    }
    
    /**
     * Sets the value of the specified key.
     * @param key Key for a new value. Can't be null.
     * @param value New value. Can't be null.
     * @return Previous value associated with the key or null if the key is new.
     * @throws IllegalArgumentException If key or value is null.
     * @throws ColumnFormatException If types of columns in Storeable aren't equal to
     * types of columns of table.
     */
    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        checkTableIsNotRemoved();
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        //TODO how to check length of Storeable?
        //Check Storeable structure.
        try {
            for (int i = 0; i < structure.size(); i++) {
                if (structure.get(i) != value.getColumnAt(i).getClass()) {
                    throw new ColumnFormatException("Storeable has a wrong "
                            + "column format");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new ColumnFormatException("Storeable has a wrong "
                    + "column format: " + e.getMessage(), e);
        }
        //End of checking section.
        Storeable oldValue;
        if (!diff.containsKey(key)) {
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
            oldValue = diff.remove(key);
        }
        diff.put(key, value);
        return oldValue;
    }
    
    /**
     * Removes the associated with the key value.
     * @param key Key for looking for value. Can't be null.
     * @return Removed value or null if there wasn't such key in this table.
     * @throws IllegalArgumentException If key is null.
     */
    @Override
    public Storeable remove(String key) {
        checkTableIsNotRemoved();
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        Storeable removedValue;
        if (!diff.containsKey(key)) {
            TablePart part = parts.get(getHash(key));
            if (part == null) {
                removedValue = null;
            } else {
                try {
                    removedValue = part.get(key);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                diff.put(key, null);
            }
        } else {
            removedValue = diff.remove(key);
        }
        return removedValue;
    }
    
    /**
     * @return Number of records stored in this table.
     */
    @Override
    public int size() {
        checkTableIsNotRemoved();
        int numberOfRecords = 0;
        for (Entry<Long, TablePart> part : parts.entrySet()) {
            numberOfRecords += part.getValue().getNumberOfRecords();
        }
        for (Entry<String, Storeable> pair : diff.entrySet()) {
            if (pair.getValue() == null) {
                numberOfRecords--;
            } else {
                numberOfRecords++;
            }
        }
        return numberOfRecords;
    }
    
    /**
     * @return List of the keys stored in this table in all parts.
     */
    @Override
    public List<String> list() {
        checkTableIsNotRemoved();
        Set<String> keySet = new HashSet<>();
        for (Entry<Long, TablePart> pair : parts.entrySet()) {
            keySet.addAll(pair.getValue().list());
        }
        for (Entry<String, Storeable> pair : diff.entrySet()) {
            if (pair.getValue() == null) {
                keySet.remove(pair.getKey());
            } else {
                keySet.add(pair.getKey());
            }
        }
        List<String> list = new LinkedList<>();
        list.addAll(keySet);
        return list;
    }
    
    /**
     * @return Number of changes after the last commit.
     */
    @Override
    public int getNumberOfUncommittedChanges() {
        checkTableIsNotRemoved();
        return diff.size();
    }
    
    /**
     * Method has package-private modifier. Expects to use with TablePart class.
     * @return TableProvider which provided this table.
     */
    TableProvider getProvider() {
        checkTableIsNotRemoved();
        return provider;
    }
    
    /**
     * Read and check table directory.
     * @throws DataBaseIOException If directory checking fails.
     */
    private void readTableDir() throws DataBaseIOException {
        readSignature();
        String[] dirList = tableDirectoryPath.toFile().list();
        for (String dir : dirList) {
            Path dirPath = tableDirectoryPath.resolve(dir);
            if (!dir.matches(Helper.DIR_NAME_REGEX) || !dirPath.toFile().isDirectory()) {
                if (dir.equals(Helper.SIGNATURE_FILE_NAME) && dirPath.toFile().isFile()) {
                    //Ignore signature file.
                    continue;
                }
                throw new DataBaseIOException(String.format("File '" + dir
                        + "' is not a directory or "
                        + "doesn't match required name '[0-%1$d].dir'",
                        Helper.NUMBER_OF_PARTITIONS - 1));
            }
            String[] fileList = dirPath.toFile().list();
            if (fileList.length == 0) {
                throw new DataBaseIOException("Directory '" + dir + "' is empty.");
            }
            for (String file : fileList) {
                Path filePath = dirPath.resolve(file);
                if (!file.matches(Helper.FILE_NAME_REGEX) || !filePath.toFile().isFile()) {
                    throw new DataBaseIOException(String.format("File '" + file + "'"
                            + " in directory '" + dir + "' is not a regular file or"
                            + " doesn't match required name '[0-%1$d].dat'",
                            Helper.NUMBER_OF_PARTITIONS - 1));
                }
                int dirNumber = Integer.parseInt(dir.substring(0, dir.length() - 4));
                int fileNumber = Integer.parseInt(file.substring(0, file.length() - 4));
                TablePart part = new TablePart(this, tableDirectoryPath, dirNumber, fileNumber);
                parts.put(Helper.hashIntPairAsLong(dirNumber, fileNumber), part);
            }
        }
    }
    
    /**
     * Reads {@value #SIGNATURE_FILE_NAME} and save table signature into memory.
     * @param filePath Path to {@value #SIGNATURE_FILE_NAME} file.
     * @throws DataBaseIOException If file is corrupted or can't be read.
     */
    private void readSignature() throws DataBaseIOException {
        Path signatureFilePath = tableDirectoryPath.resolve(Helper.SIGNATURE_FILE_NAME);
        if (!signatureFilePath.toFile().isFile()) {
            throw new DataBaseIOException("Signature file '" + Helper.SIGNATURE_FILE_NAME
                    + "' is missing");
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
            throw new DataBaseIOException("Unable read signature from "
                    + signatureFilePath.toString() + ": " + e.getMessage(), e);
        }
    }
    
    /**
     * Write all data to table directory.
     * @throws DataBaseIOException If table directory cannot be cleared
     * or some files cannot be wrote. 
     */
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
