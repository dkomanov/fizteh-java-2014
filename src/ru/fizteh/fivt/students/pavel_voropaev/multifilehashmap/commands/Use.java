package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;
public final class Use {
    private static int argNum = 2;
    private static String name = "use";
    private static String usage = "Usage: use <tablename>";

    public static MultiFileTable exec(Database db, String[] param) 
            throws IllegalArgumentException, IllegalStateException, IOException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (param.length < argNum) {
            ThrowExc.notEnoughArg(name, usage);
        }
        
        MultiFileTable newTable = db.setTable(param[1]);

        if (newTable == null) {
            throw new IllegalArgumentException(param[1] + " not exists");
        }
        
        System.out.println("using " + param[1]);
        return newTable;

    
    }
}
