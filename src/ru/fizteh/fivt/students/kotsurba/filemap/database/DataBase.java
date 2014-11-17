package ru.fizteh.fivt.students.kotsurba.filemap.database;

import java.io.IOException;

public class DataBase {
    private String dataBaseDirectory;
    private DataBaseFile fileDb;

    public DataBase(final String dbDirectory) throws IOException {
        dataBaseDirectory = dbDirectory;
        fileDb = new DataBaseFile(dataBaseDirectory);
    }

    public String put(final String keyStr, final String valueStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory);
        String result = file.put(keyStr, valueStr);
        file.save();
        return result;
    }

    public String get(final String keyStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory);
        return file.get(keyStr);
    }

    public boolean remove(final String keyStr) {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory);
        boolean result = file.remove(keyStr);
        if (result) {
            file.save();
        }
        return result;
    }

    public String list() {
        DataBaseFile file = new DataBaseFile(dataBaseDirectory);
        return file.getKeyList();
    }
}
