package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */

public class DataBase {

    public String dbFileName;
    HashMap<String, String> data;

    public DataBase(String path) throws Exception {
        dbFileName = path;
        data = new HashMap<>();
        try (DbReader reader = new DbReader(dbFileName)) {
            reader.readData(data);
        }
    }

    public void sync() throws Exception {
        try (DbWriter writer = new DbWriter(dbFileName)) {
            writer.writeData(data);
        }
    }

    public int recordsNumber() {
        return data.size();
    }

}
