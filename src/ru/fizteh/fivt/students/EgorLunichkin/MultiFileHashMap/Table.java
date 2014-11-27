package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;

import java.io.File;

public class Table {
    public Table(File tabledir) throws Exception {
        tableDirectory = tabledir;
        dataBases = new DataBase[16][16];
        for (int i = 0; i < 16; ++i) {
            File subDirectory = new File(tableDirectory, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; ++j) {
                File dbFile = new File(subDirectory, String.valueOf(j) + ".dat");
                if (dbFile.exists()) {
                    dataBases[i][j] = new DataBase(dbFile.toString());
                } else {
                    dataBases[i][j] = null;
                }
            }
        }
    }

    private File tableDirectory;
    private DataBase[][] dataBases;
}
