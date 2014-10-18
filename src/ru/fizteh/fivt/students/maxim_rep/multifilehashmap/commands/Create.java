package ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.IoLib;

public class Create implements DBCommand {

    String tableName;

    public Create(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean execute() {
        if (!IoLib.tableExists(tableName)) {
            if (IoLib.createTable(tableName)) {
                System.out.println("created");
                return true;

            } else {
                System.out
                        .println("Database Error: Couldn't create new table!");
                return false;

            }

        } else {
            System.out.println(tableName + " exists");
            return false;
        }
    }

}
