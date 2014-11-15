package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.Put;

import java.io.File;

public class PutMulti extends Command {

    private String key;
    private String value;

    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }

    public PutMulti(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    public PutMulti() {}

    protected int numberOfArguments() {
        return 2;
    }

    @Override
    public void executeOnTable(Table table) throws Exception {
        int hashCode = Math.abs(key.hashCode());
        int dir = hashCode % 16;
        int file = hashCode / 16 % 16;
        Put put = new Put(key, value);
        if (table.getClass() == Table.class) {
            if (table.databases[dir][file] == null) {
                File subDir = new File(table.mainDir, String.valueOf(dir) + ".dir");
                if (!subDir.exists()) {
                    if (!subDir.mkdir()) {
                        throw new Exception("Unable to create directories in catalog");
                    }
                }
                File dbFile = new File(subDir, String.valueOf(file) + ".dat");
                if (!dbFile.exists() && !dbFile.createNewFile()) {
                    throw new Exception("Unable to create database files in catalog");
                }
                table.databases[dir][file] = new DataBase(dbFile.toString());
            }
        }
        put.startNeedInstruction(table.databases[dir][file]);
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