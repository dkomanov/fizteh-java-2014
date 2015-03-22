package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;

import static ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler.*;

public class Commit {

    public static void run (String[] args, TableManager table) throws Exception {
        if (args.length != 1) {
            throw new Exception("Commit : " + ArgNumHandler());
         }
        if (table.currentTable == null) {
            throw new Exception("Commit : " + NullTableException());
         } else {
            String jTable = table.currentTable;
            Table dBaseTable = table.basicTables.get(jTable);
            System.out.println(dBaseTable.commit());
         }
    }
}