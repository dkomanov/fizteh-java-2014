package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;

import java.util.HashMap;

public class DirtyDataBase extends DataBase {
    public DirtyDataBase() {
        db = new HashMap<String, String>();
    }

    public DirtyDataBase(HashMap<String, String> passedData) {
        db = new HashMap<String, String>(passedData);
    }

    @Override
    public void writeDataBase() {}
}
