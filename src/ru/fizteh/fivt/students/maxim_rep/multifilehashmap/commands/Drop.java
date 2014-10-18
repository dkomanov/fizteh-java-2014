package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.IoLib;

public class Drop implements DBCommand {

    String tableName;

    public Drop(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean execute() {
        if (!IoLib.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return false;
        }

        if (!IoLib.dropTable(tableName)) {
            System.out.println("Database Error: Couldn't delete table files: "
                    + tableName);
            return false;
        } else {
            if (tableName.equals(DbMain.currentTable)) {
                DbMain.currentTable = null;
            }
            System.out.println("dropped");
            return true;
        }

    }

}
