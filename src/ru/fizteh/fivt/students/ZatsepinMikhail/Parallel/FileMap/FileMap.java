package ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileMap implements Table {
    private HashMap<String, Storeable> stableData;
    private ThreadLocal<HashMap<String, Storeable>> addedData
            = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private ThreadLocal<HashMap<String, Storeable>> changedData
            = new ThreadLocal<HashMap<String, Storeable>>() {
        @Override
        protected HashMap<String, Storeable> initialValue() {
            return new HashMap<>();
        }
    };
    private ThreadLocal<HashSet<String>> removedData
            = new ThreadLocal<HashSet<String>>() {
        @Override
        protected HashSet<String> initialValue() {
            return new HashSet<>();
        }
    };
    private List<Class<?>> typeList;
    private int numberOfColumns;
    private String directoryOfTable;
    private TableProvider parent;
    private Lock lockForCommit;

    private int getNumberOfDirectory(int hash) {
        int result = hash % 16;
        if (result < 0) {
            result += 16;
        }
        return result;
    }

    private int getNumberOfFile(int hash) {
        int result = hash / 16 % 16;
        if (result < 0) {
            result += 16;
        }
        return result;
    }

    private void clearStaff() {
        removedData.set(new HashSet<>());
        addedData.set(new HashMap<>());
        changedData.set(new HashMap<>());
    }

    /**
     * Create empty Filemap
     *
     * @param newDirectory - directory of this FileMap
     * @param newTypeList - list of types (signature of table)
     */
    public FileMap(String newDirectory, List<Class<?>> newTypeList, TableProvider newParent) {
        directoryOfTable = newDirectory;
        stableData = new HashMap<>();
        typeList = newTypeList;
        numberOfColumns = typeList.size();
        parent = newParent;
        lockForCommit = new ReentrantLock();
        init();
    }

    public TableProvider getTableProvider() {
        return parent;
    }

    @Override
    public String getName() {
        return Paths.get(directoryOfTable).getFileName().toString();
    }

    @Override
    public Storeable get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (removedData.get().contains(key)) {
            return null;
        }
        if (changedData.get().containsKey(key)) {
            return changedData.get().get(key);
        }
        if (addedData.get().containsKey(key)) {
            return addedData.get().get(key);
        }
        return stableData.get(key);
    }

    @Override
    public Storeable remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (removedData.get().contains(key)) {
            return null;
        }
        if (addedData.get().containsKey(key)) {
            return addedData.get().remove(key);
        }
        if (changedData.get().containsKey(key)) {
            removedData.get().add(key);
            return changedData.get().remove(key);
        }
        if (stableData.containsKey(key)) {
            removedData.get().add(key);
        }
        return stableData.get(key);
    }

    @Override
    public Storeable put(String key, Storeable value) throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }
        TypesUtils.checkNewStorableValue(typeList, value);
        boolean wasDeleted = false;
        if (removedData.get().contains(key)) {
            removedData.get().remove(key);
            wasDeleted = true;
        }
        if (changedData.get().containsKey(key)) {
            return changedData.get().put(key, value);
        }
        if (addedData.get().containsKey(key)) {
            return addedData.get().put(key, value);
        }

        if (stableData.containsKey(key)) {
            changedData.get().put(key, value);
        } else {
            addedData.get().put(key, value);
        }

        if (wasDeleted) {
            return null;
        } else {
            return stableData.get(key);
        }
    }

    @Override
    public int size() {
        return stableData.size() + addedData.get().size() - removedData.get().size();
    }

    @Override
    public int getColumnsCount() {
        return numberOfColumns;
    }

    @Override
    public Class<?> getColumnType(int columnIndex) throws IndexOutOfBoundsException {
        return typeList.get(columnIndex);
    }

    @Override
    public int rollback() {
        int result = changedData.get().size() + removedData.get().size() + addedData.get().size();
        clearStaff();
        return result;
    }

    @Override
    public int commit() throws IOException {
        lockForCommit.lock();

        HashMap<String, Storeable> tmpAddedData = new HashMap<>(addedData.get());
        HashMap<String, Storeable> tmpBufferAdded = new HashMap<>(addedData.get());
        HashMap<String, Storeable> tmpChangedData = new HashMap<>(changedData.get());
        HashMap<String, Storeable> tmpBufferChanged = new HashMap<>(changedData.get());

        tmpAddedData.keySet().removeAll(stableData.keySet());
        tmpBufferChanged.keySet().removeAll(stableData.keySet());
        tmpAddedData.putAll(tmpBufferChanged);

        tmpChangedData.keySet().retainAll(stableData.keySet());
        tmpBufferAdded.keySet().retainAll(stableData.keySet());
        tmpChangedData.putAll(tmpBufferAdded);

        removedData.get().retainAll(stableData.entrySet());
        int result = tmpChangedData.size()
                + removedData.get().size() + tmpAddedData.size();
        stableData.keySet().removeAll(removedData.get());
        stableData.putAll(tmpChangedData);
        stableData.putAll(tmpAddedData);

        boolean allRight = true;
        if (tmpChangedData.size() + removedData.get().size() > 0) {
            Set<String> reloadKeys = removedData.get();
            reloadKeys.addAll(tmpChangedData.keySet());
            for (String oneKey : reloadKeys) {
                if (!load(oneKey, false)) {
                    allRight = false;
                }
            }
        }
        for (String oneKey : tmpAddedData.keySet()) {
            if (!load(oneKey, true)) {
                allRight = false;
            }
        }
        lockForCommit.unlock();

        clearStaff();
        if (allRight) {
            return result;
        } else {
            throw new IOException();
        }
    }

    public List<String> list() {
        ArrayList<String> keyList = new ArrayList<>(stableData.keySet());
        keyList.removeAll(removedData.get());
        keyList.addAll(addedData.get().keySet());
        return keyList;
    }

    @Override
    public int getNumberOfUncommittedChanges() {
        return addedData.get().size() + changedData.get().size() + removedData.get().size();
    }


    public boolean init() {
        String[] listOfDirectories = new File(directoryOfTable).list();
        if (listOfDirectories == null) {
            return true;
        }
        for (String oneDirectory: listOfDirectories) {
            String currentDirectory = directoryOfTable + System.getProperty("file.separator")
                    + oneDirectory;
            if (!Files.isDirectory(Paths.get(currentDirectory))) {
                continue;
            }
            String[] listOfFiles = new File(currentDirectory).list();
            for (String oneFile : listOfFiles) {
                String currentFile = currentDirectory + System.getProperty("file.separator")
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
                        System.out.println("io exception");
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
                                stableData.put(new String(key, "UTF-8"),
                                        Serializator.deserialize(this, new String(value, "UTF-8")));
                            } catch (UnsupportedEncodingException e) {
                                System.out.println("unsupported encoding");
                                return false;
                            } catch (ParseException e) {
                                System.out.println("parse exception");
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println("null pointer exception");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("file not found");
                    return false;
                } catch (BadFileException e) {
                    System.out.println("problems with database file");
                    return false;
                } catch (IOException e) {
                    System.out.println("io exception");
                    return false;
                }
            }
        }
        return true;
    }

    public boolean load(String key, boolean appendFile) {
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

        Path directoryForLoad;
        Path fileForLoad;
        directoryForLoad = Paths.get(directoryOfTable, numberOfDirectory + ".dir");
        if (!Files.exists(directoryForLoad)) {
            try {
                Files.createDirectory(directoryForLoad);
            } catch (IOException e) {
                System.out.println("error while creating directory for load");
                return false;
            }
        }

        fileForLoad = Paths.get(directoryForLoad.toString(), numberOfFile + ".dat");
        if (!Files.exists(fileForLoad)) {
            try {
                Files.createFile(fileForLoad);
            } catch (IOException e) {
                System.out.println("error while creating file for load");
                return false;
            }
        }

        try (FileOutputStream outputStream
                     = new FileOutputStream(fileForLoad.toString(), appendFile)) {
            for (String oneKey : keySet) {
                try {
                    byte[] keyByte = oneKey.getBytes("UTF-8");
                    byte[] valueByte = Serializator.serialize(this, stableData.get(oneKey)).getBytes("UTF-8");
                    outputStream.write(bufferForSize.putInt(0, keyByte.length).array());
                    outputStream.write(keyByte);
                    outputStream.write(bufferForSize.putInt(0, valueByte.length).array());
                    outputStream.write(valueByte);
                } catch (UnsupportedEncodingException e) {
                    System.out.println("unsupported encoding");
                    return false;
                } catch (IOException e) {
                    System.out.println("io exception");
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return false;
        } catch (IOException e) {
            System.out.println("io exception");
            return false;
        }

        if (!appendFile) {
            deleteEmptyFiles(directoryForLoad, fileForLoad);
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
