package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.util.Vector;

public class CmdUse implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        Table table = db.getDataBase().get(args.get(1));
        if (table == null) {
            System.out.println(args.get(1) + " does not exists");
        } else {
            System.out.println("using " + args.get(1));
            db.currentTable = table;
        }
    }
}
