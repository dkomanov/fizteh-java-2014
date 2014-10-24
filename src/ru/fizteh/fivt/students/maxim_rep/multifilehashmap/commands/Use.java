package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.TableDataMap;

public class Use implements DBCommand {

    String tableName;

    public Use(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean execute() {
        if (!DbMain.databaseExists(DbMain.databasefilePath
                + System.getProperty("file.separator") + tableName)) {
            System.out.println(tableName + " not exists");
            return false;
        } else {
            System.out.println("using " + tableName);
            if (DbMain.fileStoredStringMap != null) {
                try {
                    DbMain.fileStoredStringMap.close();
                    DbMain.currentTable = null;
                } catch (Exception e) {
                    System.err.println("Database Error");
                    return false;
                }
            }
            try {
                DbMain.fileStoredStringMap = new TableDataMap(
                        DbMain.databasefilePath, tableName);
                DbMain.currentTable = tableName;
            } catch (IOException e) {
                System.err.println("Database Error");
                return false;
            }
        }
        return true;
    }

}
