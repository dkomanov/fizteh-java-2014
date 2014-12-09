package ru.fizteh.fivt.students.MaksimovAndrey.FileMap;

import java.util.HashMap;

public class DataBase {

    private String dataBaseName;
    HashMap<String, String> needdatabase;

    public DataBase(String needpath) throws Exception {
        dataBaseName = needpath;
        needdatabase = new HashMap<String, String>();
        DataBaseReader reader = new DataBaseReader(dataBaseName);
        reader.read(needdatabase);
        reader.close();
    }

    public void rewrite() throws Exception {
        DataBaseWriter writer = new DataBaseWriter(dataBaseName);
        writer.write(needdatabase);
        writer.close();
    }
}

