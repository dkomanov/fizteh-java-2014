package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap;

import java.util.HashMap;

public class DataBase {

    public String dataBaseName;
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

    public int sizeOfneeddatabase() {
        return needdatabase.size();
    }

}

