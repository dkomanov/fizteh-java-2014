package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.util.Vector;

public class CmdDrop implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            CommandParser.fewArgs("drop");
        } else if (args.size() > 2) {
            CommandParser.tooMuchArgs("drop");
        } else {
            if (db.tables.remove(args.get(1)) == null) {
                System.out.println(args.get(1) + " does not exists");
            } else {
                System.out.println("dropped");
            }
        }
    }
}
