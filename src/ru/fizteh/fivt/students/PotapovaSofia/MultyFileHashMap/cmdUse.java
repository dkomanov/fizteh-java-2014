package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Vector;

public class cmdUse implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            commandParser.fewArgs("use");
        } else if (args.size() > 2) {
            commandParser.tooMuchArgs("use");
        } else {
            Table table = db.tables.get(args.get(1));
            if (table == null) {
                System.out.println(args.get(1) + " does not exists");
            } else {
                System.out.println("using " + args.get(1));
                db.currentTable = table;
            }
        }
    }
}
