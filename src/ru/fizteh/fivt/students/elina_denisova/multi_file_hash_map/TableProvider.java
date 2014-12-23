package ru.fizteh.fivt.students.elina_denisova.multi_file_hash_map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;


public class TableProvider {

   private Table[][] databases;
   private File mainDir;

    public Table getBase(int i, int j) {
        return databases[i][j];
    }

    public File getMainDir() {
        return mainDir;
    }

    public void addBase(int i, int j, Table t) {
        databases[i][j] = t;
    }

    public TableProvider(final File tableDir) {
       try {
           databases = new Table[16][16];
           mainDir = tableDir;

            for (int i = 0; i < 16; i++) {
                File subDir = new File(tableDir, i + ".dir");
                for (int j = 0; j < 16; j++) {
                    File dbFile = new File(subDir, j + ".dat");
                    if (dbFile.exists()) {
                        databases[i][j] = new Table(dbFile.toString());
                    } else {
                        databases[i][j] = null;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            HandlerException.handler("TableProvider: Path-string cannot be converted to a Path", e);
        } catch (IOException e) {
            HandlerException.handler("TableProvider: Problems with reading from database file", e);
        } catch (Exception e) {
            HandlerException.handler("TableProvider: Unknown error", e);
        }
    }

    public void drop() {
        try {
            for (int i = 0; i < 16; i++) {
                File subDir = new File(mainDir, i + ".dir");
                for (int j = 0; j < 16; j++) {
                    if (databases[i][j] != null) {
                        File dbFile = new File(subDir, j + ".dat");
                        if (dbFile.exists()) {
                            try {
                                Files.delete(dbFile.toPath());
                            } catch (IOException e) {
                                HandlerException.handler("TableProvider.tDrop: cannon delete database file", e);
                            }
                        }
                    }
                }
                if (subDir.exists()) {
                    try {
                        Files.delete(subDir.toPath());
                    } catch (DirectoryNotEmptyException e) {
                        HandlerException.handler("TableProvider.tDrop: cannot remove table subdirectory", e);
                    } catch (IOException e) {
                        HandlerException.handler("TableProvider.tDrop: cannot delete database subdirectory", e);
                    }
                }
            }
            try {
                Files.delete(mainDir.toPath());
            } catch (DirectoryNotEmptyException e) {
                HandlerException.handler("TableProvider.tDrop: cannot remove main table directory", e);
            } catch (IOException e) {
                HandlerException.handler("TableProvider.tDrop: cannot delete main database directory", e);
            }
        } catch (Exception e) {
            HandlerException.handler("TableProvider.tDrop: Unknown error", e);
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

    public void commit() {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (databases[i][j] != null) {
                        databases[i][j].writeInFile();
                    }
                }
            }


    }
}
