package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;

public class List {

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 1) {
            throw new Exception("List : " + ErrorHandler.ArgNumHandler());
        }
        if (table.currentTable == null) {
            throw new Exception("List : " + ErrorHandler.NullTableException());
        }
        System.out.println(String.join("; ", table.usingTable.keys.keySet()));
    }
}