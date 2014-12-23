package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import ru.fizteh.fivt.students.EgorLunichkin.filemap.DataBase;
import ru.fizteh.fivt.students.EgorLunichkin.filemap.RemoveCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class MultiRemoveCommand implements Command {
    public MultiRemoveCommand(MultiDataBase mdb, String key) {
        this.key = key;
        this.multiDataBase = mdb;
    }

    public MultiRemoveCommand(String key) {
        this.key = key;
        this.multiDataBase = null;
    }

    private MultiDataBase multiDataBase;
    private String key;

    public void run() throws Exception {
        if (multiDataBase.using == null) {
            System.out.println("no table");
        } else {
            this.runOnTable(multiDataBase.getUsing());
        }
    }

    public void runOnTable(MultiTable table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        if (table.dataBases[dir][file] == null) {
            System.out.println("not found");
        } else {
            DataBase dataBase = table.dataBases[dir][file];
            new RemoveCommand(dataBase, key).run();
            if (dataBase.dbSize() == 0) {
                File dbFile = new File(dataBase.dbPath);
                try {
                    Files.delete(dbFile.toPath());
                } catch (SecurityException | IOException ex) {
                    throw new MultiFileHashMapException("Access violation: cannot delete database file");
                }
                dataBase = null;
                int notUsedDBs = 0;
                for (int i = 0; i < 16; ++i) {
                    if (table.dataBases[dir][i] == null) {
                        ++notUsedDBs;
                    }
                }
                if (notUsedDBs == 16) {
                    try {
                        Files.delete(dbFile.getParentFile().toPath());
                    } catch (DirectoryNotEmptyException ex) {
                        throw new MultiFileHashMapException("Cannot delete database subdir: redundant files");
                    } catch (SecurityException | IOException ex) {
                        throw new MultiFileHashMapException("Access violation: cannot delete database subdir");
                    }
                }
            }
        }
    }
}
