package ru.fizteh.fivt.students.standy66_new.storage.strings;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.storage.FileMap;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by astepanov on 20.10.14.
 */
public class StringTable implements Table {
    private static final int MAX_FILE_CHUNK = 16;
    private static final int MAX_DIR_CHUNK = 16;
    private File tableDirectory;
    private Map<File, FileMap> openedFiles;

    public StringTable(File tableDirectory) {
        if (tableDirectory == null) {
            throw new IllegalArgumentException("table directory should not be null");
        }
        if (tableDirectory.isFile()) {
            throw new IllegalArgumentException("table directory should not point to a regular file");
        }
        this.tableDirectory = tableDirectory;
        //noinspection CollectionWithoutInitialCapacity
        openedFiles = new HashMap<>();

        for (int i = 0; i < MAX_DIR_CHUNK; i++) {
            File dir = new File(tableDirectory, i + ".dir");
            for (int j = 0; j < MAX_FILE_CHUNK; j++) {
                File file = new File(dir, j + ".dat");
                try {
                    //noinspection resource,ObjectAllocationInLoop
                    openedFiles.put(file, new FileMap(file));
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred", e);
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
        //noinspection resource
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            //noinspection ReturnOfNull
            return null;
        } else {
            return fm.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }
        //noinspection resource
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            //noinspection ReturnOfNull
            return null;
        } else {
            return fm.put(key, value);
        }
    }

    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        //noinspection resource
        FileMap fm = getFileMapByKey(key);
        if (fm == null) {
            //noinspection ReturnOfNull
            return null;
        } else {
            return fm.remove(key);
        }
    }

    public int unsavedChangesCount() {
        return openedFiles.values().stream().collect(Collectors.summingInt(FileMap::unsavedChangesCount));
    }

    @Override
    public int size() {
        return openedFiles.values().stream().collect(Collectors.summingInt(FileMap::size));
    }

    @Override
    public int commit() {
        int keyChangedCount = 0;
        for (FileMap fm : openedFiles.values()) {
            try {
                keyChangedCount += fm.commit();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred", e);
            }
        }
        for (int i = 0; i < 16; i++) {
            File dir = new File(tableDirectory, String.format("%d.dir", i));
            //noinspection ConstantConditions
            if (dir.exists() && dir.listFiles().length == 0) {
                //noinspection ResultOfMethodCallIgnored
                dir.delete();
            }
        }
        return keyChangedCount;
    }

    @Override
    public int rollback() {
        int keyChangedCount = 0;
        for (FileMap fm : openedFiles.values()) {
            try {
                keyChangedCount += fm.rollback();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred", e);
            }
        }
        return keyChangedCount;
    }

    @Override
    public List<String> list() {
        //noinspection CollectionWithoutInitialCapacity
        return new ArrayList<>(openedFiles.values().stream().map(FileMap::keySet)
                .reduce(new HashSet<>(), (accumulator, set) -> {
                    accumulator.addAll(set);
                    return accumulator;
                }));
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
