package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.DataBase;

import java.util.HashMap;

public class FancyDataBase extends DataBase {

    public FancyDataBase() {
        data = new HashMap<>();
    }

    public FancyDataBase(HashMap<String, String> passedData) {
        data = new HashMap<>(passedData);
    }

    public void dump() throws Exception{
    }
}
