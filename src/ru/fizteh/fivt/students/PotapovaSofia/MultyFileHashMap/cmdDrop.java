package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.util.Vector;

public class cmdDrop implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            commandParser.fewArgs("drop");
        } else if (args.size() > 2) {
            commandParser.tooMuchArgs("drop");
        } else {
            if (db.tables.remove(args.get(1)) == null) {
                System.out.println(args.get(1) + " does not exists");
            } else {
                System.out.println("dropped");
            }
        }
    }
}
