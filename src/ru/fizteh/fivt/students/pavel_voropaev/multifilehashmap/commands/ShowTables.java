package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;
public final class ShowTables {
    private static int argNum = 2;
    private static String name = "show tables";
    private static String usage = "Usage: show tables";

    public static void exec(Database db, String[] param) throws IllegalArgumentException, IllegalStateException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        
        String[] list = db.list();
        if (list.length == 0) {
            System.out.println("");
        } else {
            for (String entry : list) {
                System.out.println(entry);
            }
        }
    }
}

