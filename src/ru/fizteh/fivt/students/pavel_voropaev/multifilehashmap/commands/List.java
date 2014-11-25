package ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.commands;

import ru.fizteh.fivt.students.pavel_voropaev.multifilehashmap.*;

public final class List {
    private static int argNum = 1;
    private static String name = "list";
    private static String usage = "Usage: list";

    public static void exec(MultiFileTable db, String[] param) throws IllegalArgumentException, IllegalStateException {
        if (param.length > argNum) {
            ThrowExc.tooManyArg(name, usage);
        }
        if (db == null) {
            ThrowExc.noTable();
        }
        
        String[] list = db.list();
        if (list.length == 0) {
            System.out.println("");
        } else {
            for (int i = 0; i < list.length - 1; ++i) {
                System.out.print(list[i] + ", ");
            }
            System.out.println(list[list.length - 1]);  
        }
    }
    
}
