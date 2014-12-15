package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.DataBase;

import java.util.HashMap;

public class FancyDataBase extends DataBase {

    public FancyDataBase() {
        data = new HashMap<>();
    }

    public FancyDataBase(HashMap<String, String> passedData) {
        data = new HashMap<>(passedData);
    }

}
