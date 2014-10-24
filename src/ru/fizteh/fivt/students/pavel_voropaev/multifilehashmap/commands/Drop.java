package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;
public final class Drop {
    private static int argNum = 2;
    private static String name = "drop";
    private static String usage = "Usage: drop <tablename>";

    public static void exec(Database db, String[] param) 
            throws IllegalArgumentException, IllegalStateException, IOException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        
        boolean table = db.dropTable(param[1]);
        if (table) {
            System.out.println("dropped");
        } else {
            System.out.println(param[1] + " not exists");
        }
    }
}
