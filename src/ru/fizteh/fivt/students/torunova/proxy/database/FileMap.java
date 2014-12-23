package ru.fizteh.fivt.students.torunova.proxy.database;

import ru.fizteh.fivt.students.torunova.proxy.database.exceptions.IncorrectFileException;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by nastya on 19.10.14.
 */
public class FileMap {
    private static final int OVERWRITTEN = 1;
    private static final int ADDED = 0;
    private static final int DELETED = 2;
    private static final int NUMBER_OF_PARTITIONS = 16;
    private static final String ENCODING = "UTF-8";
    private Map<String, String> savedCopy = new HashMap<>();
    private final ThreadLocal<Map<String, String>> diff = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };
    private final ThreadLocal<Map<String, Integer>> changes = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>();
        }
    };
    private String file;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    public FileMap(final String f) throws IncorrectFileException, IOException {
        file = f;
        readFile();
    }

    public String put(String key, String value) {
        readWriteLock.readLock().lock();
        try {
            String putInDiffResult = diff.get().put(key, value);
            String getFromSavedCopyResult = savedCopy.get(key);
            if (putInDiffResult != null || getFromSavedCopyResult != null) {
                changes.get().put(key, OVERWRITTEN);
            } else {
                changes.get().put(key, ADDED);
            }
            if (putInDiffResult == null && getFromSavedCopyResult != null) {
                return getFromSavedCopyResult;
            }
            return putInDiffResult;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public String get(String key) {
        readWriteLock.readLock().lock();
        try {
            String getFromDiffResult = diff.get().get(key);
            String getFromSavedCopyResult = savedCopy.get(key);
            if (getFromDiffResult == null && getFromSavedCopyResult != null) {
                return getFromSavedCopyResult;
            }
            return getFromDiffResult;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public String remove(String key) {
        readWriteLock.readLock().lock();
        try {
            String removeFromDiffResult = diff.get().remove(key);
            String getFromSavedCopy = savedCopy.get(key);
            if (removeFromDiffResult != null || getFromSavedCopy != null) {
                changes.get().put(key, DELETED);
            }
            if (removeFromDiffResult == null && getFromSavedCopy != null) {
                return getFromSavedCopy;
            }
            return removeFromDiffResult;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public Set<String> list() {
        readWriteLock.readLock().lock();
        try {
            Set<String> workingCopyKeySet = savedCopy.keySet();
            Set<String> diffKeySet = diff.get().keySet();
            Set<String> keys = new HashSet<>();
            if (changes == null || changes.get() == null) {
                keys.addAll(workingCopyKeySet);
                return keys;
            } else {
                for (String key : workingCopyKeySet) {
                    if (changes.get().get(key) == null
                            || (changes.get().get(key) != null && changes.get().get(key) != DELETED)) {
                        keys.add(key);
                    }
                }
                for (String key : diffKeySet) {
                    if (changes.get().get(key) != null && changes.get().get(key) != DELETED) {
                        keys.add(key);
                    }
                }
            }
            return keys;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public boolean isEmpty() {
        readWriteLock.readLock().lock();
        try {
            Map<String, String> newSavedCopy = new HashMap<>(savedCopy);
            for (Map.Entry<String, Integer> entry : changes.get().entrySet()) {
                if (entry.getValue() == DELETED) {
                    newSavedCopy.remove(entry.getKey());
                } else {
                    newSavedCopy.put(entry.getKey(), diff.get().get(entry.getKey()));
                }
            }
            return newSavedCopy.isEmpty();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public int size() {
        readWriteLock.readLock().lock();
        try {
            Map<String, String> newSavedCopy = new HashMap<>(savedCopy);
            for (Map.Entry<String, Integer> entry : changes.get().entrySet()) {
                if (entry.getValue() == DELETED) {
                    newSavedCopy.remove(entry.getKey());
                } else {
                    newSavedCopy.put(entry.getKey(), diff.get().get(entry.getKey()));
                }
            }
            return newSavedCopy.size();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public int rollback() {
        int numberOfRevertedChanges = countChangedEntries();
        diff.get().clear();
        changes.get().clear();
        return numberOfRevertedChanges;
    }
    public int commit() throws  IOException {
        readWriteLock.writeLock().lock();
        try {
            DataOutputStream fos = new DataOutputStream(new FileOutputStream(file));
            Set<String> keys = list();
            String valueFromSavedCopy;
            String valueFromDiff;
            for (String key : keys) {
                fos.writeInt(key.getBytes(ENCODING).length);
                fos.write(key.getBytes(ENCODING));
                valueFromDiff = diff.get().get(key);
                valueFromSavedCopy = savedCopy.get(key);
                fos.writeInt((valueFromDiff == null ? valueFromSavedCopy : valueFromDiff).getBytes(ENCODING).length);
                fos.write((valueFromDiff == null ? valueFromSavedCopy : valueFromDiff).getBytes(ENCODING));
            }
            int numberOfChangedEntries = countChangedEntries();
            Map<String, String> newSavedCopy = new HashMap<>(savedCopy);
            for (Map.Entry<String, Integer> entry : changes.get().entrySet()) {
                if (entry.getValue() == DELETED) {
                    newSavedCopy.remove(entry.getKey());
                } else {
                    newSavedCopy.put(entry.getKey(), diff.get().get(entry.getKey()));
                }
            }
            savedCopy = newSavedCopy;
            diff.get().clear();
            changes.get().clear();
            return  numberOfChangedEntries;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    public int countChangedEntries() {
        int counter = 0;
        readWriteLock.readLock().lock();
        try {
            for (Map.Entry<String, String> entry : diff.get().entrySet()) {
                if (savedCopy.get(entry.getKey()) == null || !savedCopy.get(entry.getKey()).equals(entry.getValue())) {
                    counter++;
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
        return counter;
    }
    private void readFile() throws IncorrectFileException, IOException {
        readWriteLock.writeLock().lock();
        try {
            DataInputStream fis = new DataInputStream(new FileInputStream(file));
            int length;
            while (fis.available() > 0) {
                length = fis.readInt();
                if (length >= Runtime.getRuntime().freeMemory()) {
                    throw new IncorrectFileException("Cannot load file " + file + ". Ran out of memory.");
                }
                if (length < 0) {
                    throw new IncorrectFileException("File " + file + " has wrong structure.");
                }
                byte[] key = new byte[length];
                if (fis.read(key) != length) {
                    throw new IncorrectFileException("File " + file + " has wrong structure.");
                }
                if (!checkKey(new String(key, ENCODING))) {
                    throw new IncorrectFileException("File " + file + " contains illegal key.");
                }
                length = fis.readInt();
                if (length >= Runtime.getRuntime().freeMemory()) {
                    throw new IncorrectFileException("Cannot load file " + file + ". Ran out of memory.");
                }
                if (length < 0) {
                    throw new IncorrectFileException("File " + file + " has wrong structure.");
                }
                byte[] value = new byte[length];
                if (fis.read(value) != length) {
                    throw new IncorrectFileException("File " + file + " has wrong structure.");
                }

                savedCopy.put(new String(key, ENCODING), new String(value, ENCODING));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    private int getIndexOfFile() {
        return Integer.parseInt(file.substring(file.lastIndexOf(File.separatorChar) + 1,
                file.lastIndexOf('.')));
    }
    private int getIndexOfDir() {
        File f = new File(file).getAbsoluteFile();
        String dirName = f.getParentFile().getName();
        return Integer.parseInt(dirName.substring(0, dirName.indexOf('.')));
    }
    private boolean checkKey(String key) {
        int hashcode = Math.abs(key.hashCode());
        int indexOfKeyFile = hashcode / NUMBER_OF_PARTITIONS % NUMBER_OF_PARTITIONS;
        int indexOfKeyDir = hashcode % NUMBER_OF_PARTITIONS;
        return indexOfKeyDir == getIndexOfDir() && indexOfKeyFile == getIndexOfFile();
    }

}
