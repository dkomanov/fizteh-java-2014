package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.io.File;

public class Table {
    private String name;
    private Path tablePath;
    private Map<Integer, DataFile> tableMap;

    public Table(Path argPath, String argName) throws IllegalArgumentException {
        tableMap = new TreeMap<>();
        tablePath = argPath;
        name = argName;
        String[] folders = tablePath.toFile().list();
        for (String currentFolderName : folders) {
            Path currentFolderPath = tablePath.resolve(currentFolderName);
            if (!currentFolderName.matches(TableManager.folderNamePattern)
                    || !currentFolderPath.toFile().isDirectory()) {
                throw new IllegalArgumentException("Unexpected files in directory");
            }
            String[] fileList = currentFolderPath.toFile().list();
            if (fileList.length == 0) {
                throw new IllegalArgumentException("Empty folders found");
            }
            for (String file : fileList) {
                Path filePath = currentFolderPath.resolve(file);
                if (!file.matches(TableManager.fileNamePattern)
                        || !filePath.toFile().isFile()) {
                    throw new IllegalArgumentException("Unexpected files in folder");
                }
                int folderIndex = TableManager.excludeFolderNumber(currentFolderName);
                int fileIndex = TableManager.excludeDataFileNumber(file);
                try {
                    DataFile part = new DataFile(tablePath, folderIndex, fileIndex);
                    tableMap.put(TableManager.getHash(fileIndex, folderIndex), part);
                } catch (IOException e){
                    throw new IllegalArgumentException(tablePath.resolve(
                            Paths.get(Integer.toString(folderIndex) + ".dir",
                                    Integer.toString(fileIndex) + ".dat")).toString() + " corrupted");
                }
            }
        }
    }

    protected void saveData() throws IOException {
        try {
            List<Integer> mustBeRemoved = new LinkedList<>();
            for (Entry<Integer, DataFile> part : tableMap.entrySet()) {
                part.getValue().putData();
                if (part.getValue().getSize() == 0) {
                    mustBeRemoved.add(part.getKey());
                }
            }
            mustBeRemoved.forEach(tableMap::remove);
        } catch (IOException e) {
            if (e.getMessage().isEmpty()) {
                throw new IOException("Can't save table '" + name);
            } else {
                throw e;
            }
        }
    }

    protected String get(String key) throws IOException {
        DataFile expectedDataFile = tableMap.get(TableManager.getHash(key));
        if (expectedDataFile == null) {
            return null;
        }
        return expectedDataFile.getValue(key);
    }

    protected String put(String key, String value) throws IOException {
        DataFile expectedDataFile = tableMap.get(TableManager.getHash(key));
        if (expectedDataFile == null) {
            int folderIndex = Math.abs(key.getBytes("UTF-8")[0] % TableManager.magicNumber);
            int fileIndex = Math.abs((key.getBytes("UTF-8")[0] / TableManager.magicNumber) % TableManager.magicNumber);
            expectedDataFile = new DataFile(tablePath, folderIndex, fileIndex);
            tableMap.put(TableManager.getHash(key), expectedDataFile);
        }
        return expectedDataFile.putValue(key, value);
    }

    protected List<String> list() throws IOException {
        List<String> list = new LinkedList<>();
        for (DataFile pair : tableMap.values()) {
            list.addAll(pair.list());
        }
        return list;
    }

    protected String removeKey(String key) throws IOException {
        DataFile expectedDataFile = tableMap.get(TableManager.getHash(key));
        if (expectedDataFile == null) {
            return null;
        }
        return expectedDataFile.remove(key);
    }

    protected void drop() {
        File directory = tablePath.toFile();
        deleteRecursively(directory);
        tableMap.clear();
    }

    protected int size() {
        int size = 0;
        for (Entry<Integer, DataFile> part : tableMap.entrySet()) {
            size += part.getValue().getSize();
        }
        return size;
    }

    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e){
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
