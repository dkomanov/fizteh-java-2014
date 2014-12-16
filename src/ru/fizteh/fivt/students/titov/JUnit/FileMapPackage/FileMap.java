package ru.fizteh.fivt.students.titov.JUnit.FileMapPackage;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileMap implements Table {
    private static final int MAX_NUMBER_OF_DIRS = 16;
    private static final int MAX_NUMBER_OF_FILES = 16;
    private HashMap<String, String> stableData;
    private HashMap<String, String> addedData;
    private HashMap<String, String> changedData;
    private HashSet<String> removedData;
    private String directoryOfTable;

    private int getNumberOfDirectory(int hash) {
        int result = hash % MAX_NUMBER_OF_DIRS;
        if (result < 0) {
            result += MAX_NUMBER_OF_DIRS;
        }
        return result;
    }

    private int getNumberOfFile(int hash) {
        int result = hash / MAX_NUMBER_OF_DIRS % MAX_NUMBER_OF_FILES;
        if (result < 0) {
            result += MAX_NUMBER_OF_FILES;
        }
        return result;
    }

    private void clearStaff() {
        removedData.clear();
        addedData.clear();
        changedData.clear();
    }

    public FileMap(String newDirectoryFile) {
        directoryOfTable = newDirectoryFile;
        stableData = new HashMap<>();
        addedData = new HashMap<>();
        changedData = new HashMap<>();
        removedData = new HashSet<>();
    }

    public String getName() {
        return Paths.get(directoryOfTable).getFileName().toString();
    }

    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (removedData.contains(key)) {
            return null;
        }
        if (changedData.containsKey(key)) {
            return changedData.get(key);
        }
        if (addedData.containsKey(key)) {
            return addedData.get(key);
        }
        return stableData.get(key);
    }

    public String remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (removedData.contains(key)) {
            return null;
        }
        if (addedData.containsKey(key)) {
            return addedData.remove(key);
        }
        if (changedData.containsKey(key)) {
            removedData.add(key);
            return changedData.remove(key);
        }
        if (stableData.containsKey(key)) {
            removedData.add(key);
        }
        return stableData.get(key);
    }

    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        boolean wasDeleted = false;
        if (removedData.contains(key)) {
            removedData.remove(key);
            wasDeleted = true;
        }
        if (changedData.containsKey(key)) {
            return changedData.put(key, value);
        }
        if (addedData.containsKey(key)) {
            return addedData.put(key, value);
        }

        if (stableData.containsKey(key)) {
            changedData.put(key, value);
        } else {
            addedData.put(key, value);
        }

        if (wasDeleted) {
            return null;
        } else {
            return stableData.get(key);
        }
    }

    public int size() {
        return stableData.size() + addedData.size() - removedData.size();
    }

    public int rollback() {
        int result =  getNumberOfUncommitedChanges();
        clearStaff();
        return result;
    }

    public int commit() {
        int result = changedData.size() + removedData.size() + addedData.size();
        stableData.keySet().removeAll(removedData);
        stableData.putAll(changedData);
        stableData.putAll(addedData);
        boolean allRight = true;
        if (changedData.size() + removedData.size() > 0) {
            Set<String> resaveKeys = removedData;
            resaveKeys.addAll(changedData.keySet());
            for (String oneKey : resaveKeys) {
                if (!save(oneKey, false)) {
                    allRight = false;
                }
            }
        }
        for (String oneKey : addedData.keySet()) {
            if (!save(oneKey, true)) {
                allRight = false;
            }
        }

        clearStaff();
        if (allRight) {
            return result;
        } else {
            return -1;
        }
    }

    public List<String> list() {
        ArrayList<String> keyList = new ArrayList<>(stableData.keySet());
        keyList.removeAll(removedData);
        keyList.addAll(addedData.keySet());
        return keyList;
    }

    public int getNumberOfUncommitedChanges() {
        return addedData.size() + changedData.size() + removedData.size();
    }

    public boolean init() {
        String[] listOfDirectories = new File(directoryOfTable).list();
        if (listOfDirectories == null) {
            return true;
        }
        for (String oneDirectory: listOfDirectories) {
            String currentDirectory = directoryOfTable + File.separator
                    + oneDirectory;
            if (!Files.isDirectory(Paths.get(currentDirectory))) {
                continue;
            }
            String[] listOfFiles = new File(currentDirectory).list();
            for (String oneFile : listOfFiles) {
                String currentFile = currentDirectory + File.separator
                        + oneFile;
                int numberOfDirectory = oneDirectory.charAt(0) - '0';
                if (oneDirectory.charAt(1) != '.') {
                    numberOfDirectory = 10 * numberOfDirectory + oneDirectory.charAt(1) - '0';
                }
                int numberOfFile = oneFile.charAt(0) - '0';
                if (oneFile.charAt(1) != '.') {
                    numberOfFile = 10 * numberOfFile + oneFile.charAt(1) - '0';
                }
                try (FileInputStream inStream = new FileInputStream(currentFile)) {
                    FileChannel inputChannel;
                    inputChannel = inStream.getChannel();
                    ByteBuffer bufferFromDisk;
                    try {
                        bufferFromDisk =
                                inputChannel.map(MapMode.READ_ONLY, 0, inputChannel.size());
                    } catch (IOException e) {
                        System.err.println("io exception");
                        return false;
                    }
                    try {
                        while (bufferFromDisk.hasRemaining()) {
                            byte[] key;
                            byte[] value;
                            int keySize;
                            int valueSize;
                            if (bufferFromDisk.remaining() >= 4) {
                                keySize = bufferFromDisk.getInt();
                                key = new byte[keySize];
                            } else {
                                throw new BadFileException();
                            }

                            if (keySize < 0) {
                                throw new BadFileException();
                            }

                            if (bufferFromDisk.remaining() >= keySize) {
                                bufferFromDisk.get(key, 0, key.length);
                            } else {
                                throw new BadFileException();
                            }

                            if (bufferFromDisk.remaining() >= 4) {
                                valueSize = bufferFromDisk.getInt();
                                value = new byte[valueSize];
                            } else {
                                throw new BadFileException();
                            }
                            if (bufferFromDisk.remaining() >= valueSize) {
                                bufferFromDisk.get(value, 0, value.length);
                            } else {
                                throw new BadFileException();
                            }

                            String keyString = new String(key, "UTF-8");
                            if (getNumberOfDirectory(keyString.hashCode()) != numberOfDirectory
                                    || getNumberOfFile(keyString.hashCode()) != numberOfFile) {
                                throw new BadFileException();
                            }

                            try {
                                stableData.put(new String(key, "UTF-8"), new String(value, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                System.err.println("unsupported encoding");
                                return false;
                            }
                        }
                    } catch (NullPointerException e) {
                        System.err.println("null pointer exception");
                    }
                } catch (FileNotFoundException e) {
                    System.err.println("file not found");
                    return false;
                } catch (BadFileException e) {
                    System.err.println("problems with database file");
                    return false;
                } catch (IOException e) {
                    System.err.println("io exception");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean save(String key, boolean appendFile) {
        HashSet<String> keySet = new HashSet<>();
        ByteBuffer bufferForSize = ByteBuffer.allocate(4);

        int numberOfDirectory = getNumberOfDirectory(key.hashCode());
        int numberOfFile = getNumberOfFile(key.hashCode());
        if (appendFile) {
            keySet.clear();
            keySet.add(key);
        } else {
            Set<String> keySetFromDB = stableData.keySet();
            for (String oneKey : keySetFromDB) {
                if (numberOfDirectory == getNumberOfDirectory(oneKey.hashCode())
                        & numberOfFile == getNumberOfFile(oneKey.hashCode())) {
                    keySet.add(oneKey);
                }
            }
        }

        Path directoryForsave;
        Path fileForsave;
        directoryForsave = Paths.get(directoryOfTable, numberOfDirectory + ".dir");
        if (!Files.exists(directoryForsave)) {
            try {
                Files.createDirectory(directoryForsave);
            } catch (IOException e) {
                System.err.println("error while creating directory for save");
                return false;
            }
        }

        fileForsave = Paths.get(directoryForsave.toString(), numberOfFile + ".dat");
        if (!Files.exists(fileForsave)) {
            try {
                Files.createFile(fileForsave);
            } catch (IOException e) {
                System.err.println("error while creating file for save");
                return false;
            }
        }

        try (FileOutputStream outputStream
                     = new FileOutputStream(fileForsave.toString(), appendFile)) {
            for (String oneKey : keySet) {
                try {
                    byte[] keyByte = oneKey.getBytes("UTF-8");
                    byte[] valueByte = stableData.get(oneKey).getBytes("UTF-8");
                    outputStream.write(bufferForSize.putInt(0, keyByte.length).array());
                    outputStream.write(keyByte);
                    outputStream.write(bufferForSize.putInt(0, valueByte.length).array());
                    outputStream.write(valueByte);
                } catch (UnsupportedEncodingException e) {
                    System.err.println("unsupported encoding");
                    return false;
                } catch (IOException e) {
                    System.err.println("io exception");
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            return false;
        } catch (IOException e) {
            System.err.println("io exception");
            return false;
        }

        if (!appendFile) {
            deleteEmptyFiles(directoryForsave, fileForsave);
        }
        return true;
    }


    public boolean deleteEmptyFiles(Path directory, Path file) {
        try {
            if (Files.size(file) == 0) {
                Files.delete(file);
            }
        } catch (IOException e) {
            System.err.println("error while deleting data base file");
            return false;
        }
        String[] listOfFiles = new File(directory.toString()).list();
        if (listOfFiles.length == 0) {
            try {
                Files.delete(directory);
            } catch (IOException e) {
                System.err.println("error while deleting directory");
                return false;
            }
        }
        return true;
    }
}
