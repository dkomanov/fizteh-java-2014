package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Vector;

public class cmdGet implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            commandParser.fewArgs("get");
        } else if (args.size() > 2) {
            commandParser.tooMuchArgs("get");
        } else {
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
}
