package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileMapState {
    public Map<String, String> dataBase;
    public File dataFile;
    public final Map<String, String> getDataBase() {
        return dataBase;
    }
    public final File getDataFile() {
        return dataFile;
    }
    public FileMapState(final File currentFile) {
        dataBase = new HashMap<String, String>();
        dataFile = currentFile;
    }
    public void setDataBase(Map<String, String> map) {
        dataBase = map;
    }
}
