package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.DataBase;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public class FancyDataBase extends DataBase {

    public FancyDataBase() {
        data = new HashMap<>();
    }

    public FancyDataBase(HashMap<String, String> passedData) {
        data = new HashMap<>(passedData);
    }

    public void sync() throws Exception{
    }
}
