package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.File;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class Create implements DBCommand {

    private String tableName;

    public Create(String tableName) {
        this.tableName = tableName;
    }

    private static boolean createTable(String tableName) throws Exception {
        File f = new File(DbMain.getTablePath(tableName));
        if (!f.exists()) {
            f.mkdir();
            return true;
        } else if (f.exists() && f.isDirectory()) {
            return false;
        }
        throw new Exception("Database Error: Failed to create table folder!");
    }

    @Override
    public boolean execute() {
        if (!DbMain.databaseExists(DbMain.databaseFilePath)) {
            System.out
                    .println("Database error: Database folder doesn't exists!");
            return false;
        }

        try {
            if (createTable(tableName)) {
                System.out.println("created");
                return true;

            } else {
                System.out.println(tableName + " exists");
                return true;

            }
        } catch (Exception e) {
            System.err.println("Database Error: Couldn't create new table!");
            return false;
        }

    }
}
