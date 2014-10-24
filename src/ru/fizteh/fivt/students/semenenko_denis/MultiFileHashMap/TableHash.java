package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by denny_000 on 11.10.2014.
 */
public class TableHash implements TableInterface {

    public static final int COUNT_OF_DIRECTORIES = 16;
    public static final int COUNT_OF_DAT_FILES = 16;


    private Path tableDirectoryPath;
    private String tableName;
    private Map<String, String> uncommitedKeys = new HashMap<>();
    private Map<String, String> deletedKeys = new HashMap<>();
    private TableFileDAT[][] datFiles;

    public TableHash(String name, Path databasePath) {
        tableDirectoryPath = databasePath.resolve(name);
        datFiles = new TableFileDAT[COUNT_OF_DIRECTORIES][];
        for (int i = 0; i < COUNT_OF_DIRECTORIES; ++i) {
            datFiles[i] = new TableFileDAT[COUNT_OF_DAT_FILES];
            for (int j = 0; j < COUNT_OF_DAT_FILES; ++j) {
                datFiles[i][j] = new TableFileDAT(tableDirectoryPath, i, j);
            }
        }
        tableName = name;
    }

    public void commit() {
        for (int i = 0; i < COUNT_OF_DIRECTORIES; ++i) {
            for (int j = 0; j < COUNT_OF_DAT_FILES; ++j) {
                datFiles[i][j].write(datFiles[i][j].getBinFile());
            }
            Path dirPath = getTableDirectoryPath().resolve(i + ".dir");
            File dir = dirPath.toFile();
            if (dir.exists()) {
                File[] listOfFiles = dir.listFiles();
                if (listOfFiles.length == 0) {
                    dir.delete();
                }
            }
        }
    }

    @Override
    public String put(String key, String value) {
        TableFileDAT datFile = selectDATFile(key);
        datFile.put(key, value);
        uncommitedKeys.put(key, value);
        return null;
    }

    @Override
    public String get(String key) {
        TableFileDAT datFile = selectDATFile(key);
        datFile.get(key);
        return null;
    }

    @Override
    public boolean remove(String key) {
        TableFileDAT datFile = selectDATFile(key);
        String oldValue = datFile.getValue(key);
        datFile.remove(key);
        deletedKeys.put(key, oldValue);
        return false;
    }

    @Override
    public List<String> list() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < COUNT_OF_DIRECTORIES; ++i) {
            for (int j = 0; j < COUNT_OF_DAT_FILES; ++j) {
                result.addAll(datFiles[i][j].list());
            }
        }
        String joined = String.join(", ", result);
        System.out.println(joined);
        return result;
    }

    public String getTableName() {
        return tableName;
    }

    public int getCount() {
        int result = 0;
        for (int i = 0; i < COUNT_OF_DIRECTORIES; ++i) {
            for (int j = 0; j < COUNT_OF_DAT_FILES; ++j) {
                result += datFiles[i][j].getCount();
            }
        }
        return result;
    }

    private TableFileDAT selectDATFile(String key) {
        int hashcode = key.hashCode();
        int directoryNumber = hashcode % 16;
        int fileNumber = hashcode / 16 % 16;
        if (directoryNumber < 0) {
            directoryNumber += 16;
        }
        if (fileNumber < 0) {
            fileNumber += 16;
        }
        return datFiles[directoryNumber][fileNumber];
    }

    public Path getTableDirectoryPath() {
        return tableDirectoryPath;
    }

    private void delete(String pathFile) {
        File file = new File(pathFile);
        file.delete();
    }
}
