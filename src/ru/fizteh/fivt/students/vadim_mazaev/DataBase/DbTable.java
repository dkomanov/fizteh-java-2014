package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

/**
 * Represents the data table containing pairs key-value. The keys must be unique.
 * Transactionality: changes are committed or rolled back using the methods {link #commit ()} or {link #rollback ()}.
 * It is assumed that among calls of these methods there aren't any I/O operations.
 * This interface is not thread safe.
 * @author Vadim Mazaev
 */
public final class DbTable implements Table {
    public static final int NUMBER_OF_PARTITIONS = 16;
    public static final String ENCODING = "UTF-8";
    private static final String FILE_NAME_REGEX = "([0-9]|1[0-5])\\.dat";
    private static final String DIR_NAME_REGEX = "([0-9]|1[0-5])\\.dir";
    private static final String SIGNATURE_FILE_NAME = "signature.tsv";
    public static final Map<String, Class<?>> TYPE_NAMES_MAP;
    static {
        Map<String, Class<?>> unitializerMap = new HashMap<>();
        unitializerMap.put("int", Integer.class);
        unitializerMap.put("long", Long.class);
        unitializerMap.put("byte", Byte.class);
        unitializerMap.put("float", Float.class);
        unitializerMap.put("double", Double.class);
        unitializerMap.put("boolean", Boolean.class);
        unitializerMap.put("String", String.class);
        TYPE_NAMES_MAP = Collections.unmodifiableMap(unitializerMap);
    }
    private TableProvider provider;
    private String name;
    //TODO rename
    private Path tableDirPath;
    private List<Class<?>> signature;
    private Map<Integer, TablePart> parts;
    private Map<String, Storeable> diff;
    
    /**
     * @param provider Link to TableProvider, which created the table. 
     * @param tableDirPath Path to table directory.
     * @throws DataBaseIOException If table directory is corrupted.
     * @throws IllegalArgumentException If 
     */
    public DbTable(TableProvider provider, Path tableDirPath)
            throws DataBaseIOException {
        parts = new HashMap<>();
        diff = new HashMap<>();
        signature = new ArrayList<>();
        if (provider == null || tableDirPath == null) {
            //TODO rewrite message
            throw new IllegalArgumentException("Provider can't be null");
        }
        this.provider = provider;
        this.tableDirPath = tableDirPath;
        this.name = tableDirPath.getName(tableDirPath.getNameCount() - 1).toString();
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
        int savedChangesCounter = diff.size();
        try {
            for (Entry<String, Storeable> pair : diff.entrySet()) {
                TablePart part = parts.get(getDirFileCode(pair.getKey()));
                if (pair.getValue() == null) {
                    part.remove(pair.getKey());
                } else {
                    if (part == null) {
                        //TODO вынести в отдельный метод
                        int dirNumber = Math.abs(pair.getKey().getBytes(ENCODING)[0]
                                % NUMBER_OF_PARTITIONS);
                        int fileNumber = Math.abs((pair.getKey().getBytes(ENCODING)[0]
                                / NUMBER_OF_PARTITIONS) % NUMBER_OF_PARTITIONS);
                        part = new TablePart(this, tableDirPath, dirNumber, fileNumber);
                        parts.put(getDirFileCode(pair.getKey()), part);
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
        int rolledChangesCounter = diff.size();
        diff.clear();
        return rolledChangesCounter;
    }
    
    /**
     * @return Name of the table.
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * @return Number of columns in table. 
     */
    @Override
    public int getColumnsCount() {
        return signature.size();
    }

    /**
     * @param columnIndex Index of column. Starts from zero.
     * @return Class representing type of value.
     * @throws IndexOutOfBoundsException Wrong index of column.
     */
    @Override
    public Class<?> getColumnType(int columnIndex)
            throws IndexOutOfBoundsException {
        if (columnIndex < 0 || columnIndex >= signature.size()) {
            throw new IndexOutOfBoundsException();
        }
        return signature.get(columnIndex);
    }
    
    /**
     * Returns file and directory numbers where the key should be placed.
     * @param key Key.
     * @return Integer code of file number and its directory number.
     */
    private int getDirFileCode(String key) {
        //TODO переписать!
        int dirNumber;
        int fileNumber;
        try {
            dirNumber = Math.abs(key.getBytes(ENCODING)[0] % NUMBER_OF_PARTITIONS);
            fileNumber = Math.abs((key.getBytes(ENCODING)[0] / NUMBER_OF_PARTITIONS)
                % NUMBER_OF_PARTITIONS);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode key to " + ENCODING, e);
        }
        return dirNumber * NUMBER_OF_PARTITIONS + fileNumber;
    }
    
    /**
     * Gets the value of the specified key.
     * @param key The key is for searching the value. Can not be null.
     *            For indexes on non-string fields parameter is a serialized column value.
     *            It is required to parse.
     * @return Value. If not found, returns null.
     * @throws IllegalArgumentException If the parameter key is null.
     * @throws RuntimeException If {@link TablePart#get(String) TablePart.get()}
     * method fails.
     */
    @Override
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        Storeable value;
        if (diff.containsKey(key)) {
            value = diff.get(key);
        } else {
            TablePart part = parts.get(getDirFileCode(key));
            if (part == null) {
                value = null;
            } else {
                try {
                    value = part.get(key);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return value;
    }
    
    /**
     * Sets the value of the specified key.
     * @param key Key.
     * @param value Value.
     * @return Previous value associated with the key or null if the key is new.
     * @throws RuntimeException If {@link TablePart#put(String, String) TablePart.put()}
     * method fails.
     */
    @Override
    public Storeable put(String key, Storeable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        Storeable oldValue;
        if (!diff.containsKey(key)) {
            TablePart part = parts.get(getDirFileCode(key));
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
     * @param key Key.
     * @return Removed value or null if there wasn't such key in this table.
     * @throws RuntimeException If {@link TablePart#remove(String) TablePart.remove()}
     * method fails.
     */
    @Override
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        Storeable removedValue;
        if (!diff.containsKey(key)) {
            TablePart part = parts.get(getDirFileCode(key));
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
        int numberOfRecords = 0;
        for (Entry<Integer, TablePart> part : parts.entrySet()) {
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
     * @throws RuntimeException If {@link TablePart#list()} method fails.
     */
    @Override
    public List<String> list() {
        Set<String> keySet = new HashSet<>();
        for (Entry<Integer, TablePart> pair : parts.entrySet()) {
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
        return diff.size();
    }
    
    /**
     * Method has package-private modifier. Expects to use with TablePart class.
     * @return TableProvider which provided this table.
     */
    TableProvider getProvider() {
        return provider;
    }
    
    /**
     * Read and check table directory.
     * @throws DataBaseIOException If directory checking fails.
     */
    private void readTableDir() throws DataBaseIOException {
        String[] dirList = tableDirPath.toFile().list();
        Path signatureFilePath = tableDirPath.resolve(SIGNATURE_FILE_NAME);
        if (!signatureFilePath.toFile().isFile()) {
            throw new DataBaseIOException("Signature file '" + SIGNATURE_FILE_NAME
                    + "' is missing");
        }
        readSignature(signatureFilePath);
        for (String dir : dirList) {
            Path dirPath = tableDirPath.resolve(dir);
            if (!dir.matches(DIR_NAME_REGEX) || !dirPath.toFile().isDirectory()) {
                if (dir.equals(SIGNATURE_FILE_NAME) && dirPath.toFile().isFile()) {
                    //Ignore signature file.
                    continue;
                }
                //TODO check the text
                throw new DataBaseIOException(String.format("File '" + dir
                        + "' is not a directory or "
                        + "doesn't match required name '[0-%1$d].dir'",
                        NUMBER_OF_PARTITIONS - 1));
            }
            String[] fileList = dirPath.toFile().list();
            if (fileList.length == 0) {
                throw new DataBaseIOException("Directory '" + dir + "' is empty.");
            }
            for (String file : fileList) {
                Path filePath = dirPath.resolve(file);
                if (!file.matches(FILE_NAME_REGEX) || !filePath.toFile().isFile()) {
                    throw new DataBaseIOException(String.format("File '" + file + "'"
                            + "in directory '" + dir + "' is not a regular file or"
                            + "doesn't match required name '[0-%1$d].dat'",
                            NUMBER_OF_PARTITIONS - 1));
                }
                //TODO вынести нахрен
                int dirNumber = Integer.parseInt(dir.substring(0, dir.length() - 4));
                int fileNumber = Integer.parseInt(file.substring(0, file.length() - 4));
                TablePart part = new TablePart(this, tableDirPath, dirNumber, fileNumber);
                parts.put(dirNumber * NUMBER_OF_PARTITIONS + fileNumber, part);
            }
        }
    }
    
    /**
     * Reads {@value #SIGNATURE_FILE_NAME} and save table signature into memory.
     * @param filePath Path to {@value #SIGNATURE_FILE_NAME} file.
     * @throws DataBaseIOException If file is corrupted or can't be read.
     */
    private void readSignature(Path filePath) throws DataBaseIOException {
        try (Scanner scanner = new Scanner(filePath)) {
            //TODO check!
            String[] types = scanner.nextLine().split("\\s+");
            for (String typeName : types) {
                Class<?> typeClass = TYPE_NAMES_MAP.get(typeName);
                if (typeClass == null) {
                    throw new DataBaseIOException("Unable read signature from " + filePath.toString()
                            + ": file contains wrong type names");
                }
                signature.add(typeClass);
            }
        } catch (IOException | NoSuchElementException e) {
            throw new DataBaseIOException("Unable read signature from " + filePath.toString(), e);
        }
    }
    
    /**
     * Write all data to table directory.
     * @throws DataBaseIOException If table directory cannot be cleared
     * or some files cannot be wrote. 
     */
    private void writeTableToDir() throws DataBaseIOException {
        Iterator<Entry<Integer, TablePart>> it = parts.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, TablePart> part = it.next();
            part.getValue().commit();
            if (part.getValue().getNumberOfRecords() == 0) {
                it.remove();
            }
        }
    }
}
