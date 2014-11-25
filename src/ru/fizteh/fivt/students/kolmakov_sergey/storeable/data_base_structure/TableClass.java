package ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_structure;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.kolmakov_sergey.storeable.data_base_exceptions.DatabaseCorruptedException;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kolmakov_sergey.storeable.util.CastMaker;
import ru.fizteh.fivt.students.kolmakov_sergey.storeable.util.Coordinates;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

public class TableClass implements Table {

    private final String name;
    private final Path tablePath;
    private final Map<Coordinates, DataFile> tableMap;
    private final Map<String, Storeable> difference; // (null in Value) -> this entry must be removed.
    private final String unexpectedFilesMessage = "Unexpected files found in directory ";
    private final String emptyFoldersMessage = "Empty folders found";
    private final String signatureFileName = "signature.tsv";
    private final TableProvider tableProvider;
    private List<Class<?>> columnTypes;

    public TableClass(Path tablePath, String name, TableProvider tableProvider, List<Class<?>> columnTypes)
            throws DatabaseCorruptedException {
        this.tableProvider = tableProvider;
        tableMap = new HashMap<>();
        difference = new HashMap<>();
        this.tablePath = tablePath;
        this.name = name;

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
                    writer.println(CastMaker.classToString(currentClass));
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
                int folderIndex = TableManager.excludeFolderNumber(currentFolderName);
                int fileIndex = TableManager.excludeDataFileNumber(currentFileName);
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
        List<String> list = new ArrayList<>();
        boolean fileIsEmpty = true;
        try (Scanner in = new Scanner(new File(currentFolderPath.toString()))) {
            while (in.hasNextLine()) {
                list.add(in.nextLine());
                fileIsEmpty = false;
            }
            if (fileIsEmpty) {
                throw new DatabaseCorruptedException(signatureFileName + " is corrupted in table " + name);
            }
        } catch (FileNotFoundException e) {
            throw new DatabaseCorruptedException("Can't find " + signatureFileName + " in table " + name);
        }
        try {
            for (String currentString : list) {
                columnTypes.add(CastMaker.stringToClass(currentString));
            }
        } catch (IllegalArgumentException e) {
            throw new DatabaseCorruptedException(signatureFileName + " is corrupted in table " + name);
        }
    }

    @Override
    public int commit() {
        int numberOfChanges = difference.size();
        try { // Apply difference to tableMap.
            for (Entry<String, Storeable> currentOperation : difference.entrySet()) {
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
    public Storeable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("get: null key");
        }
        Storeable value;
        if (difference.containsKey(key)) {
            value = difference.get(key);
        } else {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
            if (expectedDataFile == null) {
                value = null;
            } else {
                value = expectedDataFile.get(key);
            }
        }
        return value;
    }

    @Override
    public Storeable put(String key, Storeable value) throws ColumnFormatException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("put: null arguments");
        }
        Storeable oldValue;
        if (!difference.containsKey(key)) {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
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
    public Storeable remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("remove: key == null");
        }
        Storeable removedValue;
        if (!difference.containsKey(key)) {
            DataFile expectedDataFile = tableMap.get(new Coordinates(key, TableManager.NUMBER_OF_PARTITIONS));
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
        for (Entry<String, Storeable> currentOperation : difference.entrySet()) {
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
    public int getNumberOfUncommittedChanges() {
        return difference.size();
    }

    @Override
    public int getColumnsCount() {
        return columnTypes.size();
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        try {
            return columnTypes.get(columnIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Column with index " + columnIndex + " doesn't exist in this table");
        }
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
        for (Entry<String, Storeable> pair : difference.entrySet()) {
            if (pair.getValue() == null) {
                numberOfRecords--;
            } else {
                numberOfRecords++;
            }
        }
        return numberOfRecords;
    }
}
