package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Size {
    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 1) {
            throw new Exception("Size : " + ErrorHandler.ArgNumHandler());
        }
        String jTable = table.currentTable;
        System.out.println(table.basicTables.get(jTable).size());
    }
}