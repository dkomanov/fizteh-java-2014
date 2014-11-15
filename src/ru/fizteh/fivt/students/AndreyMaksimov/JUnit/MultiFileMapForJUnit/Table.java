package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.DataBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class Table {
    public DataBase[][] databases;
    File mainDir;

    public Table(File tableDir) throws Exception {
        databases = new DataBase[16][16];
        mainDir = tableDir;
        for (int i = 0; i < 16; i++) {
            File subDir = new File(tableDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                File dbFile = new File(subDir, String.valueOf(j) + ".dat");
                if (dbFile.exists()) {
                    databases[i][j] = new DataBase(dbFile.toString());
                } else {
                    databases[i][j] = null;
                }
            }
        }
    }

    public Table() {}

    public void drop() throws Exception {
        for (int i = 0; i < 16; i++) {
            File subDir = new File(mainDir, String.valueOf(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                if (databases[i][j] != null) {
                    File dbFile = new File(subDir, String.valueOf(j) + ".dat");
                    if (dbFile.exists()) {
                        try {
                            Files.delete(dbFile.toPath());
                        } catch (SecurityException | IOException e) {
                            throw new Exception("Access violation: cannon delete database file");
                        }
                    }
                }
            }
            if (subDir.exists()) {
                try {
                    Files.delete(subDir.toPath());
                } catch (DirectoryNotEmptyException e) {
                    throw new Exception("Cannot remove table subdirectory. Redundant files");
                } catch (SecurityException | IOException e) {
                    throw new Exception("Access violation: cannot delete database subdirectory");
                }
            }
        }
        try {
            Files.delete(mainDir.toPath());
        } catch (DirectoryNotEmptyException e) {
            throw new Exception("Cannot remove main table directory. Redundant files");
        } catch (SecurityException | IOException e) {
            throw new Exception("Access violation: cannot delete main database directory");
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

