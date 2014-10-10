package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public final class Table {
    public Table(Path tableDirPath, String name) {
        parts = new TreeMap<Integer, TablePart>();
        this.tableDirPath = tableDirPath;
        this.name = name;
        numberOfRecords = 0;
        isConnected = false;
        try {
            readTableDir(true);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading table '" + getName()
                    + "': directory is corrupted");
        }
    }
    
    private void connect() {
        System.err.println("<debug> Table '" + getName() + "' connect method was called");
        try {
            readTableDir(false);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading table '" + getName()
                    + "': directory is corrupted");
        }
        isConnected = true;
    }
    
    public void commit() throws IOException {
        System.err.println("<debug> Table '"
                + getName() + "' commit method was called");
        try {
            if (isConnected) {
                writeTableToDir();
                //TODO checks
                isConnected = false;
            }
        } catch (IOException e) {
            throw new IOException("Error writing table '" + getName()
                    + "' to directory");
        }
    }
    
    public String getName() {
        return name;
    }
    
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
    
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        if (!isConnected) {
            connect();
        }
        return parts.get(getDirFileNumbersCode(key)).get(key);
    }
    
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is a null-string");
        }
        if (!isConnected) {
            connect();
        }
        String prevValue = parts.get(getDirFileNumbersCode(key)).put(key, value);
        if (prevValue == null) {
            numberOfRecords++;
        }
        return prevValue;
    }
    
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is a null-string");
        }
        if (!isConnected) {
            connect();
        }
        String oldValue = parts.get(getDirFileNumbersCode(key)).remove(key);
        if (oldValue != null) {
            numberOfRecords--;
        }
        return oldValue;
    }
    
    public int size() {
        return numberOfRecords; 
    }
    
    public List<String> list() {
        if (!isConnected) {
            connect();
        }
        List<String> list = new LinkedList<String>();
        for (Entry<Integer, TablePart> pair : parts.entrySet()) {
            list.addAll(pair.getValue().list());
        }
        return list;
    }

    public void deleteTable() throws IOException {
        clearTableDir();
        parts.clear();
        tableDirPath.toFile().delete();
    }
    
    private void readTableDir(boolean onlyGetNumberOfRecords) throws IOException {
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
                if (!onlyGetNumberOfRecords) {
                    TablePart part = new TablePart(tableDirPath, dirNumber, fileNumber);
                    parts.put(dirNumber * 100 + fileNumber, part);
                } else {
                    numberOfRecords += TablePart
                            .getNumberOfRecords(tableDirPath, dirNumber, fileNumber);
                }
            }
        }
    }
    
    private void clearTableDir() {
        String[] dirList = tableDirPath.toFile().list();
        for (String curDir : dirList) {
            String[] fileList = tableDirPath.resolve(curDir).toFile().list();
            for (String file : fileList) {
                Paths.get(tableDirPath.toString(), curDir, file).toFile().delete();
            }
            tableDirPath.resolve(curDir).toFile().delete();
        }
    }
    
    private void writeTableToDir() throws IOException {
        clearTableDir();
        for (Entry<Integer, TablePart> part : parts.entrySet()) {
            int dirNumber = part.getKey() / 100;
            tableDirPath.resolve(Integer.valueOf(dirNumber).toString() + ".dir").toFile().mkdir();
            part.getValue().writeToFile();
        }
    }
    
    private String name;
    private Path tableDirPath;
    private boolean isConnected;
    private int numberOfRecords;
    private Map<Integer, TablePart> parts;
}
