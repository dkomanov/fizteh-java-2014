package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.DataBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class Table {
    DataBase[][] databases;
    File mainDirectory;

    public Table(File tableDirectory) throws Exception {
        databases = new DataBase[16][16];
        mainDirectory = tableDirectory;
        for (int i = 0; i < 16; i++) {
            File subdirectory = new File(tableDirectory, String.valueOf(i) + "dir");
            for (int j = 0; j < 16; j++) {
                File dbFile = new File(subdirectory, String.valueOf(j) + ".dat");
                if (dbFile.exists()) {
                    databases[i][j] = new DataBase(dbFile.toString());
                } else {
                    databases[i][j] = null;
                }
            }
        }
    }

    public void drop() throws Exception {
        for (int i = 0; i < 16; i++) {
            File subdirectory = new File(mainDirectory, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                if (databases[i][j] != null) {
                    File dbFile = new File(subdirectory, String.valueOf(j) + ".dat");
                    if (dbFile.exists()) {
                        try {
                            Files.delete(dbFile.toPath());
                        } catch (IOException e) {
                            throw new Exception("ERROR: Problems while deleting the file");
                        }
                    }
                }
            }
            if (subdirectory.exists()) {
                try {
                    Files.delete(subdirectory.toPath());
                } catch (IOException e) {
                    throw new Exception("ERROR: Problems while deleting the database subdirectory");
                }
            }
        }
        try {
            Files.delete(mainDirectory.toPath());
        } catch (IOException e) {
            throw new Exception("ERROR: Problems while deleting the mainDirectory");
        }
    }

    public int recordsNumber() {
        int result = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (databases[i][j] != null) {
                    result += databases[i][j].sizeOfneeddatabase();
                }
            }
        }
        return result;
    }
}


