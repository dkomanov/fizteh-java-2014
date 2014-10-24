package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Vector;

public class cmdPut implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 3) {
            commandParser.fewArgs("put");
        } else if (args.size() > 3) {
            commandParser.tooMuchArgs("put");
        } else {
            if (db.currentTable == null) {
                System.out.println("no table");
                return;
            }
            String key = args.get(1);
            String value = args.get(2);
            if (db.currentTable.put(key, value) != null) {
                System.out.println("overwrite");
            } else {
                System.out.println("new");
            }
        }
    }
}
