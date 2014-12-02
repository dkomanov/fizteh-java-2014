package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Table;

public class DampingTable extends Table {

    public DampingTable() {
        databases = new DampingDataBase[16][16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                databases[i][j] = new DampingDataBase();
            }
        }
    }

    public void importMap(Table other) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (other.databases[i][j] == null) {
                    databases[i][j] = new DampingDataBase();
                } else {
                    databases[i][j] = new DampingDataBase(other.databases[i][j].data);
                }
            }
        }
    }
}
