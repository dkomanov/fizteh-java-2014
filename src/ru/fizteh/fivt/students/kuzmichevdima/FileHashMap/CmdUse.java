package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.util.Vector;

public class CmdUse implements Command {
    @Override
    public void execute(Vector<String> args, DB db) {
        Table table = db.getDataBase().get(args.get(1));
        if (table == null) {
            System.out.println(args.get(1) + " not exists");
        } else {
            System.out.println("using " + args.get(1));
            db.currentTable = table;
        }
    }
}
