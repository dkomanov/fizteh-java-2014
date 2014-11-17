package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class Table {
    DataBaseOneFile[][] databases;
    File insideMainDir;

    public Table(File pathToInsideMainDir) throws Exception {
        databases = new DataBaseOneFile[16][16];
        insideMainDir = pathToInsideMainDir;
        for (int i = 0; i < 16; i++) {
            File doubleInsideMainDir = new File(pathToInsideMainDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                File dataBaseFile = new File(doubleInsideMainDir, String.valueOf(j) + ".dat");
                if (dataBaseFile.exists()) {
                    databases[i][j] = new DataBaseOneFile(dataBaseFile.toString());
                } else {
                    databases[i][j] = null;
                }
            }
        }
    }

    public void drop() throws Exception {
        for (int i = 0; i < 16; i++) {
            File doubleInsideMainDir = new File(insideMainDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                if (databases[i][j] != null) {
                    File dataBaseFile = new File(doubleInsideMainDir, String.valueOf(j) + ".dat");
                    if (dataBaseFile.exists()) {
                        try {
                            Files.delete(dataBaseFile.toPath());
                        } catch (IOException e) {
                            throw new Exception("Cann't delete database file");
                        }
                    }
                }
            }
            if (doubleInsideMainDir.exists()) {
                try {
                    Files.delete(doubleInsideMainDir.toPath());
                } catch (DirectoryNotEmptyException e) {
                    throw new Exception("Cann't delete directory. There are directories inside this");
                }
            }
        }
        try {
            Files.delete(insideMainDir.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw new Exception("Cann't delete directory. There are directories inside this");
        }
    }

    public int recordsNumber() {
        int answer = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (databases[i][j] != null) {
                    answer += databases[i][j].recordsNumber();
                }
            }
        }
        return answer;
    }
}
