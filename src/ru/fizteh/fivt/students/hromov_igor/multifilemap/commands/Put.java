package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import java.nio.file.Path;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Put {

    private static final int SIZE = 16;

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 3) {
            throw new Exception("Put : " + ErrorHandler.argNumHandler());
        }
        String tableName = table.currentTable;
        if (table.currentTable == null) {
            throw new Exception("Table : " + ErrorHandler.nullTableException());
        }
        Path path = table.path;
        String key = args[1];
        String value = args[2];
        table.usingTable.puted.put(key, value);
        if (table.usingTable.keys.containsKey(key)) {
            System.out.println("overwrite");
            System.out.println(table.usingTable.keys.get(key));
        } else {
            System.out.println("new");
        }
    }
}
