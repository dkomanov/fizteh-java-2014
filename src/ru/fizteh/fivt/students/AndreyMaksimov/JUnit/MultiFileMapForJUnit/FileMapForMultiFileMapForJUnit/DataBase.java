package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit;

import java.util.HashMap;

public class DataBase {

    public String dataBaseFileName;
    public HashMap<String, String> data;

    public DataBase(String path) throws Exception {
        dataBaseFileName = path;
        data = new HashMap<>();
        try (DataBaseReader reader = new DataBaseReader(dataBaseFileName)) {
            reader.read(data);
        }
    }

    public DataBase() {
    }

    public void sync() throws Exception {
        try (DataBaseWriter writer = new DataBaseWriter(dataBaseFileName)) {
            writer.write(data);
        }
    }

    public int recordsNumber() {
        return data.size();
    }
}
