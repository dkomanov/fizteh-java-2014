package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.TableDataMap;

public class Use implements DBCommand {

    private String tableName;

    public Use(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean execute() {

        if (!DbMain.databaseExists(DbMain.getTablePath(tableName))) {
            System.out.println(tableName + " not exists");
            return false;
        } else {
            System.out.println("using " + tableName);
            if (DbMain.fileStoredStringMap != null) {
                try {
                    DbMain.fileStoredStringMap.close();
                } catch (Exception e) {
                    System.err.println("Use Database Error: " + e.toString());
                    return false;
                }
            }
            try {
                DbMain.fileStoredStringMap = new TableDataMap(
                        DbMain.databaseFilePath, tableName);
            } catch (IOException e) {
                System.err.println("Use Database Error: " + e.toString());
                return false;
            }
        }
        return true;
    }

}
