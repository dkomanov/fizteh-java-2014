package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Table;

public class FancyTable extends Table {

    public FancyTable() {
        databases = new FancyDataBase[NUM_DIRECTORIES][NUM_FILES];
        for (int i = 0; i < NUM_DIRECTORIES; i++) {
            for (int j = 0; j < NUM_FILES; j++) {
                databases[i][j] = new FancyDataBase();
            }
        }
    }

    public void importMap(Table other) {
        for (int i = 0; i < NUM_FILES; i++) {
            for (int j = 0; j < NUM_DIRECTORIES; j++) {
                if (other.databases[i][j] == null) {
                    databases[i][j] = new FancyDataBase();
                } else {
                    databases[i][j] = new FancyDataBase(other.databases[i][j].data);
                }
            }
        }
    }
}
