package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;

public final class Remove {
    private static int argNum = 2;
    private static String name = "remove";
    private static String usage = "Usage: remove <key>";

    public static void exec(MultiFileTable db, String[] param) throws IllegalArgumentException, IllegalStateException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        if (db == null) {
            ThrowExc.noTable();
        }
        
        if (db.remove(param[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
    
}
