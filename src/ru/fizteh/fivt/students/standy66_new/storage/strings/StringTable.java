package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.CheckedExceptionCaughtException;
import ru.fizteh.fivt.students.standy66_new.storage.FileMap;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringTable implements Table {
    private static final int MAX_FILE_CHUNK = 16;
    private static final int MAX_DIR_CHUNK = 16;
    private final File tableDirectory;
    private final Map<File, FileMap> openedFiles;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public StringTable(File tableDirectory) {
        if (tableDirectory == null) {
            throw new IllegalArgumentException("table directory should not be null");
        }
        if (tableDirectory.isFile()) {
            throw new IllegalArgumentException("table directory should not point to a regular file");
        }
        this.tableDirectory = tableDirectory;

        openedFiles = new HashMap<>();

        for (int i = 0; i < MAX_DIR_CHUNK; i++) {
            File dir = new File(tableDirectory, i + ".dir");
            for (int j = 0; j < MAX_FILE_CHUNK; j++) {
                File file = new File(dir, j + ".dat");
                try {
                    openedFiles.put(file, new FileMap(file));
                } catch (IOException e) {
                    throw new CheckedExceptionCaughtException("IOException occurred", e);
                }
            }
        }
    }

    public File getFile() {
        return tableDirectory;
    }

    @Override
    public String getName() {
        return tableDirectory.getName();
    }

    @Override
    public String get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }

        FileMap fm = getFileMapByKey(key);
        readWriteLock.readLock().lock();
        try {
            return (fm == null) ? null : fm.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }

        FileMap fm = getFileMapByKey(key);
        readWriteLock.writeLock().lock();
        try {
            return (fm == null) ? null : fm.put(key, value);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        FileMap fm = getFileMapByKey(key);
        readWriteLock.writeLock().lock();
        try {
            return (fm == null) ? null : fm.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public int unsavedChangesCount() {
        readWriteLock.readLock().lock();
        try {
            return openedFiles.values().stream().collect(Collectors.summingInt(FileMap::unsavedChangesCount));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        readWriteLock.readLock().lock();
        try {
            return openedFiles.values().stream().collect(Collectors.summingInt(FileMap::size));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public int commit() {
        readWriteLock.writeLock().lock();
        try {
            int keyChangedCount = 0;
            for (FileMap fm : openedFiles.values()) {
                try {
                    keyChangedCount += fm.commit();
                } catch (IOException e) {
                    throw new CheckedExceptionCaughtException("IOException occurred", e);
                }
            }
            for (int i = 0; i < MAX_DIR_CHUNK; i++) {
                File dir = new File(tableDirectory, String.format("%d.dir", i));

                if (dir.exists() && (dir.listFiles() != null) && (dir.listFiles().length == 0)) {
                    dir.delete();
                }
            }
            return keyChangedCount;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public int rollback() {
        readWriteLock.writeLock().lock();
        try {
            int keyChangedCount = 0;
            for (FileMap fm : openedFiles.values()) {
                try {
                    keyChangedCount += fm.rollback();
                } catch (IOException e) {
                    throw new CheckedExceptionCaughtException("IOException occurred", e);
                }
            }
            return keyChangedCount;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<String> list() {
        readWriteLock.readLock().lock();
        try {
            return new ArrayList<>(openedFiles.values().stream().map(FileMap::keySet)
                    .reduce(new HashSet<>(), (accumulator, set) -> {
                        accumulator.addAll(set);
                        return accumulator;
                    }));
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private File getChunkFileByKey(String key) {
        int hashcode = key.hashCode();
        int nDirectory = Integer.remainderUnsigned(hashcode, MAX_DIR_CHUNK);
        int nFile = Integer.remainderUnsigned((hashcode / MAX_DIR_CHUNK), MAX_FILE_CHUNK);
        File dir = new File(tableDirectory, nDirectory + ".dir");
        return new File(dir, nFile + ".dat");
    }

    private FileMap getFileMapByKey(String key) {
        File chunkFile = getChunkFileByKey(key);
        return openedFiles.get(chunkFile);
    }
}
