package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class CmdList implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }
        Set keySet = db.currentTable.keySet();
        String joined = String.join(", ", keySet);
        System.out.println(joined);
    }
}
