package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.File;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;

public class Create implements DBCommand {

    String tableName;

    public Create(String tableName) {
        this.tableName = tableName;
    }

    private static boolean createTable(String tableName) throws Exception {
        File f = null;
        f = new File(DbMain.databasefilePath
                + System.getProperty("file.separator") + tableName);
        if (!f.exists()) {
            f.mkdir();
            return true;
        } else if (f.exists() && f.isDirectory()) {
            return false;
        }
        throw new Exception();
    }

    @Override
    public boolean execute() {

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
