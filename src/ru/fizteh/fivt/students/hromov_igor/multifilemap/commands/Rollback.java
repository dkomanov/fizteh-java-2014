package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Rollback {

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 1) {
            throw new Exception("Rollback : " + ErrorHandler.argNumHandler());
        }
        if (table.currentTable == null) {
            throw new Exception("Table : " + ErrorHandler.nullTableException());
        }
        String jTable = table.currentTable;
        Table dBaseTable = table.basicTables.get(jTable);
        System.out.println(dBaseTable.rollback());
        table.saved = true;
    }
}
