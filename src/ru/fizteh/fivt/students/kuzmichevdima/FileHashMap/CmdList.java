package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.util.Set;
import java.util.Vector;

public class CmdList implements Command {
    @Override
    public void execute(Vector<String> args, DB db) {
        if (db.currentTable == null) {
            System.out.println("no table");
            return;
        }
        Set<String> keySet = db.currentTable.keySet();
        String joined = String.join(", ", keySet);
        System.out.println(joined);
    }
}
