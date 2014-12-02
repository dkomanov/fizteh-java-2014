package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiTable;

public class DirtyTable extends MultiTable {
    public DirtyTable() {
        dataBases = new DirtyDataBase[16][16];
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                dataBases[dir][file] = new DirtyDataBase();
            }
        }
    }

    public void importData(MultiTable other) {
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                if (other.dataBases[dir][file] == null) {
                    dataBases[dir][file] = new DirtyDataBase();
                } else {
                    dataBases[dir][file] = new DirtyDataBase(other.dataBases[dir][file].getDataBase());
                }
            }
        }
    }
}
