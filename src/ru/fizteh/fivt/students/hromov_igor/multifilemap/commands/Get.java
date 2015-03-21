package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Get {

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 2) {
            throw new Exception("Get : " + ErrorHandler.ArgNumHandler());
        }

        if (table.currentTable == null) {
            throw new Exception("Table : " + ErrorHandler.NullTableException());
        }

        String key = args[1];

        if (table.usingTable.keys.containsKey(key)) {
            System.out.println("found");
            System.out.println(table.usingTable.keys.get(key));
        } else {
            System.out.println("not found");
        }
    }
}
