package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Table;

import java.util.HashMap;

/**
 * @author AlexeyZhuravlev
 */
public class FancyTable extends Table {

    public FancyTable() {
        databases = new FancyDataBase[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                databases[i][j] = new FancyDataBase();
            }
        }
    }

    private void addMap(int i, int j, HashMap<String, String> map) {
        databases[i][j].data = new HashMap<>(map);
    }

    public void importMap(Table other) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (other.databases[i][j] != null) {
                    addMap(i, j, other.databases[i][j].data);
                }
            }
        }
    }
}
