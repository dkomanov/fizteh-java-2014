package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a data table.
 * @author Vadim Mazaev
 */
public final class Table {
    private String name;
    private Path tableDirPath;
    private Map<Integer, TablePart> parts;
    
    /**
     * Constructs a Table which represents a table directory on disk.
     * @see {@link Table#readTableDir readTableDir} to know about reading and checking data.
     * @param tableDirPath Path to table directory. Doesn't check it.
     * @param name Table name. Doesn't check for equality of table name and directory name. 
     * @throws IllegalArgumentException If table directory is corrupted.
     */
    public Table(Path tableDirPath, String name) {
        parts = new HashMap<Integer, TablePart>();
        this.tableDirPath = tableDirPath;
        this.name = name;
        try {
            readTableDir();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading table '" + getName()
                    + "': directory is corrupted");
        }
    }
    
    /**
     * Writes all data from {@link TablePart}s to table directory
     * @throws IOException If cannot write data to files.
     */
    public void commit() throws IOException {
//        System.err.println("<debug> Table '"
//                + getName() + "' commit method was called");
        try {
            writeTableToDir();
        } catch (IOException e) {
            throw new IOException("Error writing table '" + getName()
                    + "' to directory");
        }
    }
    
    /**
     * @return Name of the table.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns file and directory numbers where the key should be placed.
     * @param key Key.
     * @return Integer code of file number and its directory number in such format: ddff.
     */
    private int getDirFileNumbersCode(String key) {
        int dirNumber = 0;
        int fileNumber = 0;
        try {
            dirNumber = Math.abs(key.getBytes("UTF-8")[0] % 16);
            fileNumber = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode key to UTF-8");
        }
        return dirNumber * 100 + fileNumber;
    }
    
    /**
     * Gets the value of the specified key.
     * @param key Key.
     * @return Value, if key contains in this table, otherwise null.
     * @throws IOException If {@link TablePart#get(String) TablePart.get()}
     * method fails.
     */
    public String get(String key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        TablePart part = parts.get(getDirFileNumbersCode(key));
        if (part == null) {
            return null;
        }
        return part.get(key);
    }
    
    /**
     * Sets the value of the specified key.
     * @param key Key.
     * @param value Value.
     * @return Previous value associated with the key or null if the key is new.
     * @throws IOException If {@link TablePart#put(String, String) TablePart.put()}
     * method fails.
     */
    public String put(String key, String value) throws IOException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        TablePart part = parts.get(getDirFileNumbersCode(key));
        if (part == null) {
            int dirNumber = Math.abs(key.getBytes("UTF-8")[0] % 16);
            int fileNumber = Math.abs((key.getBytes("UTF-8")[0] / 16) % 16);
            part = new TablePart(tableDirPath, dirNumber, fileNumber);
            parts.put(dirNumber * 100 + fileNumber, part);
        }
        return part.put(key, value);
    }
    
    /**
     * Removes the associated with the key value.
     * @param key Key.
     * @return Removed associated value.
     * @throws IOException If {@link TablePart#remove(String) TablePart.remove()}
     * method fails.
     */
    public String remove(String key) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        TablePart part = parts.get(getDirFileNumbersCode(key));
        if (part == null) {
            return null;
        }
        return part.remove(key);
    }
    
    /**
     * @return Number of records stored in this table.
     */
    public int size() {
        int numberOfRecords = 0;
        for (Entry<Integer, TablePart> part : parts.entrySet()) {
            numberOfRecords += part.getValue().getNumberOfRecords();
        }
        return numberOfRecords;
    }
    
    /**
     * @return List of the keys stored in this table in all parts.
     * @throws IOException If {@link TablePart#list()} method fails.
     */
    public List<String> list() throws IOException {
        List<String> list = new LinkedList<String>();
        for (Entry<Integer, TablePart> pair : parts.entrySet()) {
            list.addAll(pair.getValue().list());
        }
        return list;
    }

    /**
     * Deletes all {@link TablePart}s of this table and table directory.
     * @throws IOException If directory cannot be deleted.
     */
    public void deleteTable() throws IOException {
        String[] dirList = tableDirPath.toFile().list();
        for (String curDir : dirList) {
            String[] fileList = tableDirPath.resolve(curDir).toFile().list();
            for (String file : fileList) {
                Paths.get(tableDirPath.toString(), curDir, file).toFile().delete();
            }
            tableDirPath.resolve(curDir).toFile().delete();
        }
        tableDirPath.toFile().delete();
        parts.clear();
    }
    
    /**
     * Reads table directory, checks it, creates new {@link TablePart}
     * for every file in directory.
     * @see
     * {@link TablePart#TablePart(Path tableDirPath, int dirNumber, int fileNumber)
     * TablePart Constructor} to know about saving data in memory.  
     * @throws IOException If directory checking fails.
     */
    private void readTableDir() throws IOException {
        String[] dirList = tableDirPath.toFile().list();
        for (String dir : dirList) {
            Path curDir = tableDirPath.resolve(dir);
            if (!dir.matches("([0-9]|1[0-5])\\.dir")
                    || !curDir.toFile().isDirectory()) {
                throw new IOException();
            }
            String[] fileList = curDir.toFile().list();
            if (fileList.length == 0) {
                throw new IOException();
            }
            for (String file : fileList) {
                Path filePath = curDir.resolve(file);
                if (!file.matches("([0-9]|1[0-5])\\.dat")
                        || !filePath.toFile().isFile()) {
                    throw new IOException();
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
     * @throws IOException If table directory cannot be cleared
     * or some files cannot be wrote. 
     */
    private void writeTableToDir() throws IOException {
        for (Entry<Integer, TablePart> part : parts.entrySet()) {
            part.getValue().disconnect();
            if (part.getValue().getNumberOfRecords() == 0) {
                parts.remove(part.getKey());
            }
        }
    }
}
