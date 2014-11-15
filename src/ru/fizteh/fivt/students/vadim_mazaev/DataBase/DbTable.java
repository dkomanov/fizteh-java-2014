package ru.fizteh.fivt.students.vadim_mazaev.DataBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * Represents a data table.
 * @author Vadim Mazaev
 */
public final class DbTable implements Table {
    private String name;
    private Path tableDirPath;
    private Map<Integer, TablePart> parts;
    private Map<String, String> diff;
    
    /**
     * Constructs a Table which represents a table directory on disk.
     * @see {@link DbTable#readTableDir readTableDir} to know about reading and checking data.
     * @param tableDirPath Path to table directory. Method doesn't check it.
     * @param name Table name. Doesn't check for equality of table name and directory name. 
     * @throws RuntimeException If table directory is corrupted.
     */
    public DbTable(Path tableDirPath, String name) {
        parts = new HashMap<>();
        diff = new HashMap<>();
        this.tableDirPath = tableDirPath;
        this.name = name;
        try {
            readTableDir();
        } catch (DataBaseIOException e) {
            throw new RuntimeException("Error reading table '" + getName()
                    + "': " + e.getMessage(), e);
        }
    }
    
    /**
     * Writes all data from {@link TablePart}s to table directory
     * @return Number of saved changes.
     * @throws RuntimeException If cannot write data to files.
     */
    @Override
    public int commit() {
        int savedChangesCounter = diff.size();
        try {
            for (Entry<String, String> pair : diff.entrySet()) {
                TablePart part = parts.get(getDirFileCode(pair.getKey()));
                if (pair.getValue() == null) {
                    part.remove(pair.getKey());
                } else {
                    if (part == null) {
                        int dirNumber = Math.abs(pair.getKey().getBytes("UTF-8")[0] % 16);
                        int fileNumber = Math.abs((pair.getKey().getBytes("UTF-8")[0] / 16) % 16);
                        part = new TablePart(tableDirPath, dirNumber, fileNumber);
                        parts.put(dirNumber * 100 + fileNumber, part);
                    }
                    part.put(pair.getKey(), pair.getValue());
                }
            }
            diff.clear();
            writeTableToDir();
        } catch (IOException | DataBaseIOException e) {
            throw new RuntimeException("Error writing table '" + getName()
                    + "' to its directory: " + e.getMessage(), e);
        }
        return savedChangesCounter;
    }
    
    /** 
     * Rolls back the changes to the last commit, reading the disk.
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
     * Returns file and directory numbers where the key should be placed.
     * @param key Key.
     * @return Integer code of file number and its directory number in such format: ddff.
     */
    private int getDirFileCode(String key) {
        int dirNumber = 0;
        int fileNumber = 0;
        try {
            dirNumber = Math.abs(key.getBytes("UTF-8")[0] % 16);
            fileNumber = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode key to UTF-8", e);
        }
        return dirNumber * 100 + fileNumber;
    }
    
    /**
     * Gets the value of the specified key.
     * @param key Key.
     * @return Value, if key contains in this table, otherwise null.
     * @throws RuntimeException If {@link TablePart#get(String) TablePart.get()}
     * method fails.
     */
    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        String value;
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
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        String oldValue;
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
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        String removedValue;
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
        for (Entry<String, String> pair : diff.entrySet()) {
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
        for (Entry<String, String> pair : diff.entrySet()) {
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
    public int getNumberOfChanges() {
        return diff.size();
    }
    
    /**
     * Reads table directory, checks it, creates new {@link TablePart}
     * for every file in directory.
     * @see
     * {@link TablePart#TablePart(Path tableDirPath, int dirNumber, int fileNumber)
     * TablePart Constructor} to know about saving data in memory.  
     * @throws DataBaseIOException If directory checking fails.
     */
    private void readTableDir() throws DataBaseIOException {
        String[] dirList = tableDirPath.toFile().list();
        for (String dir : dirList) {
            Path curDir = tableDirPath.resolve(dir);
            if (!dir.matches("([0-9]|1[0-5])\\.dir")
                    || !curDir.toFile().isDirectory()) {
                throw new DataBaseIOException("File '" + dir
                        + "' is not a directory or doesn't match required name '[0-15].dir'", null);
            }
            String[] fileList = curDir.toFile().list();
            if (fileList.length == 0) {
                throw new DataBaseIOException("Directory '" + dir + "' is empty.", null);
            }
            for (String file : fileList) {
                Path filePath = curDir.resolve(file);
                if (!file.matches("([0-9]|1[0-5])\\.dat")
                        || !filePath.toFile().isFile()) {
                    throw new DataBaseIOException("File '" + file + "' in directory '" + dir
                            + "' is not a file or doesn't match required name '[0-15].dat'", null);
                }
                int dirNumber = Integer.parseInt(dir.substring(0, dir.length() - 4));
                int fileNumber = Integer.parseInt(file.substring(0, file.length() - 4));
                TablePart part = new TablePart(tableDirPath, dirNumber, fileNumber);
                parts.put(dirNumber * 100 + fileNumber, part);
            }
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
