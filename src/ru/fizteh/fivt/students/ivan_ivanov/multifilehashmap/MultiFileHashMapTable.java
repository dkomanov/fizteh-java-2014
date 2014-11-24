package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultiFileHashMapTable {

    private Map<String, String> dataBase;
    private File dataFile;

    public MultiFileHashMapTable(final File currentFile) throws IOException {

        dataBase = new HashMap<>();
        dataFile = currentFile;
        MultiFileHashMapUtils.read(currentFile, dataBase);
    }

    public final Map<String, String> getDataBase() {
        return dataBase;
    }

    public final File getDataFile() {
        return dataFile;
    }

    public final String getName() {

        return dataFile.getName();
    }

    public final String get(final String key) {

        return dataBase.get(key);
    }

    public final String put(final String key, final String value) {

        return dataBase.put(key, value);
    }

    public final String remove(final String key) {

        return dataBase.remove(key);
    }

}
