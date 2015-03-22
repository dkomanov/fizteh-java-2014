package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Remove {

    private static final int SIZE = 16;

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 2) {
            throw new Exception("Remove : " + ErrorHandler.argNumHandler());
        }
        if (table.currentTable == null) {
            throw new Exception("Table : " + ErrorHandler.nullTableException());
        }
        String key = args[1];
        if (table.usingTable.keys.containsKey(key)) {
            System.out.println("removed");
            table.usingTable.removed.add(key);
        } else {
            System.out.println("not found");
        }
    }
}
