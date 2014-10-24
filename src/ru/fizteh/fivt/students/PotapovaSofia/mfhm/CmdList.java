package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Iterator;
import java.util.Vector;

public class CmdList implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() > 1) {
            CommandParser.tooMuchArgs("list");
        } else {
            if (db.currentTable == null) {
                System.out.println("no table");
                return;
            }
            if (db.currentTable.size() > 0) {
                Iterator<String> it = db.currentTable.keySet().iterator();
                System.out.print(it.next());
                while (it.hasNext()) {
                    System.out.print(", " + it.next());
                }
            }
            System.out.println("");
        }
    }
}
