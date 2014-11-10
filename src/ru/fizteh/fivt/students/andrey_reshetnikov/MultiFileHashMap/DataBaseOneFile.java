package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.HashMap;

public class DataBaseOneFile {

    public String dataBaseFileName;
    HashMap<String, String> data;

    public DataBaseOneFile(String path) throws Exception {
        dataBaseFileName = path;
        data = new HashMap<>();
        DbReader smartReader = new DbReader(dataBaseFileName);
        smartReader.load(data);
    }

    public void dump() throws Exception {
        DbWriter smartWriter = new DbWriter(dataBaseFileName);
        smartWriter.writeData(data);
    }

    public int recordsNumber() {
        return data.size();
    }
}
