package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.File;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DatabaseException;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.TableDataMap;

public class Drop implements DBCommand {

    private String tableName;

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
            System.err.println((new DatabaseException("Filesystem damaged - "
                    + e.toString()).toString()));
            return false;
        }

        if (tableName.equals(DbMain.getCurrentTableName())) {
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

            System.err.println((new DatabaseException(
                    "Couldn't delete table files: " + tableName).toString()));
            return false;
        } else {
            if (tableName.equals(DbMain.getCurrentTableName())) {
                DbMain.fileStoredStringMap = null;
            }
            System.out.println("dropped");
            return true;
        }

    }

}
