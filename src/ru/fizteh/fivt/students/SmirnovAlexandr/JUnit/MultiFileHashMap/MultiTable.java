package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class MultiTable {
    public DataBase[][] databases;
    File mainDir;

    public MultiTable(File tableDir) throws Exception {
        databases = new DataBase[ConstClass.NUM_DIRECTORIES][ConstClass.NUM_FILES];
        mainDir = tableDir;
        for (int i = 0; i < ConstClass.NUM_DIRECTORIES; i++) {
            File subDir = new File(tableDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < ConstClass.NUM_FILES; j++) {
                File dbFile = new File(subDir, String.valueOf(j) + ".dat");
                if (dbFile.exists()) {
                    databases[i][j] = new DataBase(dbFile.toString());
                } else {
                    databases[i][j] = null;
                }
            }
        }
    }

    public MultiTable() {}

    public void drop() throws Exception {
        for (int i = 0; i < ConstClass.NUM_DIRECTORIES; i++) {
            File subDir = new File(mainDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < ConstClass.NUM_FILES; j++) {
                if (databases[i][j] != null) {
                    File dbFile = new File(subDir, String.valueOf(j) + ".dat");
                    if (dbFile.exists()) {
                        try {
                            dbFile.delete();
                        } catch (SecurityException e) {
                            throw new Exception("Can not delete database file");
                        }
                    }
                }
            }
            if (subDir.exists()) {
                try {
                    Files.delete(subDir.toPath());
                } catch (DirectoryNotEmptyException e) {
                    throw new Exception("Can not remove table directory. Redundant files");
                } catch (SecurityException e) {
                    throw new Exception("Can not delete database subdirectory");
                }
            }
        }
        try {
            Files.delete(mainDir.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw new Exception("Can not remove table directory. Redundant files");
        } catch (SecurityException e) {
            throw new Exception("Сan not delete main database directory");
        }
    }

    public int recordsNumber() {
        int answer = 0;
        for (int i = 0; i < ConstClass.NUM_DIRECTORIES; i++) {
            for (int j = 0; j < ConstClass.NUM_FILES; j++) {
                if (databases[i][j] != null) {
                    answer += databases[i][j].recordsNumber();
                }
            }
        }
        return answer;
    }
}
