package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileMapState {

    public Map<String, String> dataBase;
    public File dataFile;

    public Map<String, String> getDataBase() {
        return dataBase;
    }

    public File getDataFile() {
        return dataFile;
    }

    public FileMapState(File currentFile) {
        dataBase = new HashMap<String, String>();
        dataFile = currentFile;
    }
}
