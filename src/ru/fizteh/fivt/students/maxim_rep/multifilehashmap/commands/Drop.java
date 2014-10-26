package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.File;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.TableDataMap;

public class Drop implements DBCommand {

    String tableName;

    public Drop(String tableName) {
        this.tableName = tableName;
    }

    private boolean dropTable(String tableName) {
        if (!DbMain.databaseExists(DbMain.getTablePath(tableName))) {
            return false;
        }

        try {
            TableDataMap.removeFolder(new File(DbMain.getTablePath(tableName)));
        } catch (Exception e) {
            System.err.println("Database error: filesystem damaged");
            return false;
        }

        if (tableName.equals(DbMain.currentTable)) {
            DbMain.fileStoredStringMap.clear();
        }
        return true;
    }

    @Override
    public boolean execute() {
        if (!DbMain.databaseExists(DbMain.getTablePath(tableName))) {
            System.out.println(tableName + " not exists");
            return false;
        }

        if (!dropTable(tableName)) {
            System.err.println("Database Error: Couldn't delete table files: "
                    + tableName);
            return false;
        } else {
            if (tableName.equals(DbMain.currentTable)) {
                DbMain.currentTable = null;
                DbMain.fileStoredStringMap = null;
            }
            System.out.println("dropped");
            return true;
        }

    }

}
