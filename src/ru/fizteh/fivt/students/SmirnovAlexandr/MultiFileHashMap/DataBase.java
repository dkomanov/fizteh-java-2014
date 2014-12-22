package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

import java.util.HashMap;

public class DataBase {

    public String dbFileName;
    public HashMap<String, String> data;

    public DataBase(String path) throws Exception {
        dbFileName = path;
        data = new HashMap<>();
        try (DbReader reader = new DbReader(dbFileName)) {
            reader.readData(data);
        }
    }

    public DataBase() {}

    public void dump() throws Exception {
        try (DbWriter writer = new DbWriter(dbFileName)) {
            writer.writeData(data);
        }
    }

    public int recordsNumber() {
        return data.size();
    }

}
