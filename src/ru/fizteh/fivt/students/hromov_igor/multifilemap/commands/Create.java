package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.*;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.base.TableManager;
import ru.fizteh.fivt.students.hromov_igor.multifilemap.exception.ErrorHandler;

public class Create {

    public static void run(String[] args, TableManager table) throws Exception {
        if (args.length != 2) {
            throw new Exception("Create : " + ErrorHandler.argNumHandler());
        }
        table.create(args[1]);
    }
}
