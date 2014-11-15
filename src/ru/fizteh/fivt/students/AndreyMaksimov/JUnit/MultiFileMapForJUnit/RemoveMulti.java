package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.Remove;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;

public class RemoveMulti extends Command {
    private String key;

    protected void putArguments(String[] args) {
        key = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    public RemoveMulti() {}

    public RemoveMulti(String passedKey) {
        key = passedKey;
    }

    @Override
    public void executeOnTable(Table table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        Remove remove = new Remove(key);
        if (table.databases[dir][file] == null) {
            System.out.println("not found");
        } else {
            DataBase dataBase = table.databases[dir][file];
            remove.startNeedInstruction(dataBase);
            if (table.getClass() == Table.class) {
                if (dataBase.recordsNumber() == 0) {
                    File dbFile = new File(dataBase.dataBaseFileName);
                    try {
                        Files.delete(dbFile.toPath());
                    } catch (SecurityException | IOException e) {
                        throw new Exception("Access violation: cannon delete database file");
                    }
                    table.databases[dir][file] = null;
                    int k = 0;
                    for (int j = 0; j < 16; j++) {
                        if (table.databases[dir][j] == null) {
                            k++;
                        }
                    }
                    if (k == 16) {
                        try {
                            Files.delete(dbFile.getParentFile().toPath());
                        } catch (DirectoryNotEmptyException e) {
                            throw new Exception("Cannot remove table subdirectory. Redundant files");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            executeOnTable(base.getUsing());
        }
    }
}