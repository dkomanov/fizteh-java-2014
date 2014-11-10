package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_structure;

import ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.storage.strings.Table;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class TableClass implements Table {
    private String name;
    private Path tablePath;
    private Map<Coordinates, DataFile> tableMap;
    private Map<String, String> difference; // (null in Value) -> this entry must be removed.
    private final String unexpectedFilesMessage = "Unexpected files found in directory ";
    private final String emptyFoldersMessage = "Empty folders found in ";

    public TableClass(Path tablePath, String name) throws DatabaseCorruptedException {
        tableMap = new HashMap<>();
        difference = new HashMap<>();
        this.tablePath = tablePath;
        this.name = name;
        String[] folders = this.tablePath.toFile().list();
        for (String currentFolderName : folders) {
            Path currentFolderPath = this.tablePath.resolve(currentFolderName);
            if (!currentFolderName.matches(TableManager.FOLDER_NAME_PATTERN)
                    || !currentFolderPath.toFile().isDirectory()) {
                throw new DatabaseCorruptedException(unexpectedFilesMessage + tablePath.toString());
            }
            String[] fileList = currentFolderPath.toFile().list();
            if (fileList.length == 0) {
                throw new DatabaseCorruptedException(emptyFoldersMessage + tablePath.toString());
            }
            for (String currentFileName : fileList) {
                Path filePath = currentFolderPath.resolve(currentFileName);
                if (!currentFileName.matches(TableManager.FILE_NAME_PATTERN) || !filePath.toFile().isFile()) {
                    throw new DatabaseCorruptedException(unexpectedFilesMessage + tablePath.toString());
                }
                int folderIndex = TableManager.excludeFolderNumber(currentFolderName);
                int fileIndex = TableManager.excludeDataFileNumber(currentFileName);
                try {
                    DataFile currentDataFile;
                    currentDataFile = new DataFile(this.tablePath, new Coordinates(folderIndex, fileIndex));
                    tableMap.put(new Coordinates(folderIndex, fileIndex), currentDataFile);
                } catch (IOException e) {
                    throw new DatabaseCorruptedException(this.tablePath.resolve(
                            Paths.get(Integer.toString(folderIndex) + ".dir",
                                    Integer.toString(fileIndex) + ".dat")).toString() + " corrupted");
                }
            }
        }
    }

    @Override
    public int commit() {
        int numberOfChanges = difference.size();
        try { // Apply difference to tableMap.
            for (Entry<String, String> currentOperation : difference.entrySet()) {
                DataFile dataFile = tableMap.get(new Coordinates(currentOperation.getKey()));
                if (currentOperation.getValue() == null) {
                    dataFile.remove(currentOperation.getKey()); // In this case dataFile can't be null.
                } else {
                    try {
                        if (dataFile == null) {
                            dataFile = new DataFile(tablePath, new Coordinates(currentOperation.getKey()));
                            tableMap.put(new Coordinates(currentOperation.getKey()), dataFile);
                        }
                        dataFile.put(currentOperation.getKey(), currentOperation.getValue());
                    } catch (DatabaseCorruptedException e) {
                        throw new RuntimeException("Can't commit table '" + getName()
                                + "': " + e.getMessage(), e);
                    }
                }
            }
            difference.clear();
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
        }
        return numberOfChanges;
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: null key");
        }
        String value;
        if (difference.containsKey(key)) {
            value = difference.get(key);
        } else {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key));
            if (expectedDataFile == null) {
                value = null;
            } else {
                value = expectedDataFile.get(key);
            }
        }
        return value;
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: null arguments");
        }
        String oldValue;
        if (!difference.containsKey(key)) {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key));
            if (expectedDataFile == null) {
                oldValue = null;
            } else {
                oldValue = expectedDataFile.get(key);
            }
        } else {
            oldValue = difference.get(key);
        }
        difference.put(key, value);
        return oldValue;
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key == null");
        }
        String removedValue;
        if (!difference.containsKey(key)) {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key));
            if (expectedDataFile == null) {
                removedValue = null;
            } else {
                removedValue = expectedDataFile.get(key);
                difference.put(key, null);
            }
        } else {
            removedValue = difference.remove(key);
        }
        return removedValue;
    }

    @Override
    public List<String> list() {
        Set<String> mergeResult = new HashSet<>();
        for (DataFile dataFile : tableMap.values()) {
            mergeResult.addAll(dataFile.list());
        }
        for (Entry<String, String> currentOperation : difference.entrySet()) {
            if (currentOperation.getValue() == null) {
                mergeResult.remove(currentOperation.getKey());
            } else {
                mergeResult.add(currentOperation.getKey());
            }
        }
        List<String> list = new LinkedList<>();
        list.addAll(mergeResult);
        return list;
    }

    @Override
    public int rollback() {
        int numberOfChanges = difference.size();
        difference.clear();
        return numberOfChanges;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int size() {
        int numberOfRecords = 0;
        for (DataFile currentTable: tableMap.values()) {
            numberOfRecords += currentTable.size();
        }
        for (Entry<String, String> pair : difference.entrySet()) {
            if (pair.getValue() == null) {
                numberOfRecords--;
            } else {
                numberOfRecords++;
            }
        }
        return numberOfRecords;
    }

    public int numberOfChanges() {
        return difference.size();
    }
}
