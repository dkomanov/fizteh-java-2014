package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.DbMain;
import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.IoLib;

public class Use implements DBCommand {

    String tableName;

    public Use(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean execute() {
        if (!IoLib.tableExists(tableName)) {
            System.out.println(tableName + " not exists");
            return false;
        } else {
            System.out.println("using " + tableName);
            DbMain.currentTable = tableName;
        }
        return true;
    }

}
