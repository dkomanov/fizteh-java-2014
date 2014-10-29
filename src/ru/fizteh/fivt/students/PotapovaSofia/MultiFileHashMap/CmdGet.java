package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.util.Vector;

public class CmdGet implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }
        String key = args.get(1);
        String value = db.currentTable.get(key);
        if (value != null) {
            System.out.println("found\n" + value);
        } else {
            System.out.println("not found");
        }
    }
}
