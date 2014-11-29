package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

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

    public File tableDirectory;
    public DataBase[][] dataBases;

    public void drop() throws MultiFileHashMapException {
        for (int dir = 0; dir < 16; ++dir) {
            File subDirectory = new File(tableDirectory, String.valueOf(dir) + ".dir");
            for (int file = 0; file < 16; ++file) {
                if (dataBases[dir][file] != null) {
                    File dbFile = new File(subDirectory, String.valueOf(file) + ".dat");
                    if (dbFile.exists()) {
                        try {
                            Files.delete(dbFile.toPath());
                        } catch (SecurityException | IOException ex) {
                            throw new MultiFileHashMapException("Access violation: cannot delete database file");
                        }
                    }
                }
            }
            if (subDirectory.exists()) {
                try {
                    Files.delete(subDirectory.toPath());
                } catch (DirectoryNotEmptyException ex) {
                    throw new MultiFileHashMapException("Cannot delete database subdirectory: redundant files");
                } catch (SecurityException | IOException ex) {
                    throw new MultiFileHashMapException("Access violation: cannot delete database subdirectory");
                }
            }
        }
        try {
            Files.delete(tableDirectory.toPath());
        } catch (DirectoryNotEmptyException ex) {
            throw new MultiFileHashMapException("Cannot delete table directory: redundant files");
        } catch (SecurityException | IOException ex) {
            throw new MultiFileHashMapException("Access violation: cannot delete table directory");
        }
    }

    public int tableSize() {
        int size = 0;
        for (int dir = 0; dir < 16; ++dir) {
            for (int file = 0; file < 16; ++file) {
                if (dataBases[dir][file] != null) {
                    size += dataBases[dir][file].dbSize();
                }
            }
        }
        return size;
    }
}
