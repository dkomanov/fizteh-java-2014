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
    private Map<Coordinates, DataFile> tableMap;

    public Table(Path tablePath, String name) throws DatabaseCorruptedException {
        tableMap = new TreeMap<>();
        this.tablePath = tablePath;
        this.name = name;
        String[] folders = this.tablePath.toFile().list();
        for (String currentFolderName : folders) {
            Path currentFolderPath = this.tablePath.resolve(currentFolderName);
            if (!currentFolderName.matches(TableManager.FOLDER_NAME_PATTERN)
                    || !currentFolderPath.toFile().isDirectory()) {
                throw new DatabaseCorruptedException("Unexpected files in directory");
            }
            String[] fileList = currentFolderPath.toFile().list();
            if (fileList.length == 0) {
                throw new DatabaseCorruptedException("Empty folders found");
            }
            for (String file : fileList) {
                Path filePath = currentFolderPath.resolve(file);
                if (!file.matches(TableManager.FILE_NAME_PATTERN) || !filePath.toFile().isFile()) {
                    throw new DatabaseCorruptedException("Unexpected files in folder");
                }
                int folderIndex = TableManager.excludeFolderNumber(currentFolderName);
                int fileIndex = TableManager.excludeDataFileNumber(file);
                try {
                    DataFile currentDataFile = new DataFile(this.tablePath, new Coordinates(folderIndex, fileIndex));
                    tableMap.put(new Coordinates(folderIndex, fileIndex), currentDataFile);
                } catch (IOException e) {
                    throw new DatabaseCorruptedException(this.tablePath.resolve(
                            Paths.get(Integer.toString(folderIndex) + ".dir",
                                    Integer.toString(fileIndex) + ".dat")).toString() + " corrupted");
                }
            }
        }
    }

    protected void saveData() throws IOException {
        try {
            List<Coordinates> mustBeRemoved = new LinkedList<>();
            for (Entry<Coordinates, DataFile> currentFile : tableMap.entrySet()) {
                currentFile.getValue().putData();
                if (currentFile.getValue().getSize() == 0) {
                    mustBeRemoved.add(currentFile.getKey());
                }
            }
            mustBeRemoved.forEach(tableMap::remove);
        } catch (IOException e) {
            if (e.getMessage() == null || e.getMessage().isEmpty()) {
                throw new IOException("Can't save table '" + name);
            } else {
                throw e;
            }
        }
    }

    protected String get(String key) throws IOException, DatabaseCorruptedException {
        DataFile expectedDataFile = tableMap.get(new Coordinates(key));
        if (expectedDataFile == null) {
            return null;
        }
        return expectedDataFile.getValue(key);
    }

    protected String put(String key, String value) throws IOException, DatabaseCorruptedException {
        DataFile expectedDataFile = tableMap.get(new Coordinates(key));
        if (expectedDataFile == null) {
            try {
                expectedDataFile = new DataFile(tablePath, new Coordinates(key));
            } catch (DatabaseCorruptedException e) {
                // Never, because we create new table.
                System.out.println("Unexpected Exception in put!");
            }
            tableMap.put(new Coordinates(key), expectedDataFile);
        }
        return expectedDataFile.putValue(key, value);
    }

    protected List<String> list() throws IOException, DatabaseCorruptedException {
        List<String> list = new LinkedList<>();
        for (DataFile currentDataFile : tableMap.values()) {
            list.addAll(currentDataFile.list());
        }
        return list;
    }

    protected String removeKey(String key) throws IOException, DatabaseCorruptedException {
        DataFile expectedDataFile = tableMap.get(new Coordinates(key));
        if (expectedDataFile == null) {
            return null;
        }
        return expectedDataFile.remove(key);
    }

    protected void drop() {
        deleteRecursively(tablePath.toFile());
        tableMap.clear();
    }

    protected int size() {
        int size = 0;
        for (Entry<Coordinates, DataFile> currentEntry : tableMap.entrySet()) {
            size += currentEntry.getValue().getSize();
        }
        return size;
    }

    private static void deleteRecursively(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    deleteRecursively(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
