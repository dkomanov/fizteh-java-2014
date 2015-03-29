package ru.fizteh.fivt.students.hromov_igor.multifilemap.base;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.exception.DirCreateException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

class DirectoryAndFileDescriptor {
    private int directoryIndex;
    private int fileIndex;

    DirectoryAndFileDescriptor(int b, int size) {
        this.directoryIndex = b % size;
        this.fileIndex = b / size % size;
    }


    public int getDirectoryIndex() {
        return directoryIndex;
    }

    public int getFileIndex() {
        return fileIndex;
    }

}

public class DBaseTable implements Table {

    static final int SIZE = 16;

    private String dirExpansion = ".dir";
    private String fileExpansion = ".dat";
    private String tableName;
    private Path path;
    private DBaseTableChunk[][] tableDateBase;
    private Map<String, String> keys;
    private Map<String, String> puted;
    private Set<String> removed;

    public DBaseTable() {
        keys = new HashMap<>();
        puted = new HashMap<>();
        removed = new HashSet<>();
        tableDateBase = new DBaseTableChunk[SIZE][SIZE];
    }

    public DBaseTable(String name, Path pathTable) {
        this();
        tableName = name;
        path = pathTable.resolve(Paths.get(name));
    }

    public DBaseTable(DBaseTable dataBaseTable) {
        keys = dataBaseTable.keys;
        puted = dataBaseTable.puted;
        removed = dataBaseTable.removed;
        tableName = dataBaseTable.tableName;
        path = dataBaseTable.path;
        tableDateBase = dataBaseTable.tableDateBase;
    }

    public DBaseTableChunk[][] getTableDateBase() {
        return tableDateBase;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("No key, null");
        }

        if (keys.containsKey(key)) {
            return (keys.get(key));
        }
        return null;
     }

    @Override
    public int size() {
        return keys.size();
     }

    @Override
    public String put(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("No key, null");
        }
        puted.put(key, value);
        return keys.get(key);
    }

    @Override
    public String getName() {
        return tableName;
     }


    @Override
    public String remove(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (!keys.containsKey(key)) {
            return null;
        }
        removed.add(key);
        return keys.get(key);
    }


    @Override
    public int commit() {
        if (puted.size() == 0 && removed.size() == 0) {
            return 0;
         }
        for (Entry<String, String> pair : puted.entrySet()) {
            DirectoryAndFileDescriptor place = new DirectoryAndFileDescriptor(pair.getKey().getBytes()[0], SIZE);

            if (tableDateBase[place.getDirectoryIndex()][place.getFileIndex()] == null) {
                String s = String.valueOf(place.getDirectoryIndex()).concat(dirExpansion);
                Path pathDir = path;
                pathDir = pathDir.resolve(s);
                if (!pathDir.toFile().exists()) {
                    try {
                        pathDir.toFile().mkdir();
                    } catch (SecurityException e) {
                        throw new DirCreateException(e);
                    }
                }

                s = String.valueOf(place.getFileIndex()).concat(fileExpansion);
                Path pathFile = pathDir.resolve(s);
                try {
                    if (!pathFile.toFile().exists()) {
                        try {
                            pathFile.toFile().createNewFile();
                        } catch (IOException e) {
                            throw new RuntimeException("Can't create file", e);
                        }
                    }
                    tableDateBase[place.getDirectoryIndex()][place.getFileIndex()] =
                            new DBaseTableChunk(pathFile.toString());
                } catch (IOException e) {
                    throw new RuntimeException("File doesn't exist");
                }
            }
            try {
                tableDateBase[place.getDirectoryIndex()][place.getFileIndex()].
                        put(pair.getKey(), pair.getValue());
            } catch (Exception e) {
                throw new IllegalArgumentException("Table error");
            }
            if (keys.containsKey(pair.getKey())) {
                keys.remove(pair.getKey());
            }
            keys.put(pair.getKey(), pair.getValue());
        }
        int size = puted.size();
        puted.clear();
        for (String key : removed) {
            DirectoryAndFileDescriptor place = new DirectoryAndFileDescriptor(key.getBytes()[0], SIZE);
            tableDateBase[place.getDirectoryIndex()][place.getFileIndex()].remove(key);
            keys.remove(key);
        }
        removed.clear();
        try {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (tableDateBase[i][j] != null) {
                    tableDateBase[i][j].save();
                }
            }
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return size;
    }

    @Override
    public int rollback() {
        int size = puted.size();
        removed.clear();
        puted.clear();
        return size;
     }

    @Override
    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (String key : keys.keySet()) {
            list.add(key);
        }
        return list;
     }

}
