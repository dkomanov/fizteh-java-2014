package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;

public final class Exit {
    private static int argNum = 1;
    private static String name = "exit";
    private static String usage = "Usage: exit";

    public static void exec(Database db, String[] param) throws IOException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        db.close();
    }
    
}
