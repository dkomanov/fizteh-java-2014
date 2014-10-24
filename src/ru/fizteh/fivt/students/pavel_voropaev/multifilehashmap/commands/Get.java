package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;
public final class Get {
    private static int argNum = 2;
    private static String name = "get";
    private static String usage = "Usage: get <key>";

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
           
        String tmp = db.get(param[1]);
        if (tmp == null) {
            System.out.println("not found");
        } else {
            System.out.println(tmp);
        }
    }

}
