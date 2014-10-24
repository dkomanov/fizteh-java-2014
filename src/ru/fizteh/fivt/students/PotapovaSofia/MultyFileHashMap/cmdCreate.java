package ru.fizteh.fivt.students.PotapovaSofia.MultyFileHashMap;

import java.nio.file.Path;
import java.util.Vector;

public class cmdCreate implements Command {
    @Override
    public void execute(Vector<String> args, DataBase db) {
        if (args.size() < 2) {
            commandParser.fewArgs("create");
        } else if (args.size() > 2) {
            commandParser.tooMuchArgs("create");
        } else {
            String tableName = args.get(1);
            Path tablePath = db.getDbPath().resolve(tableName);
            if (db.tables.put(tableName, new Table(tablePath)) == null) {
                System.out.println("created");
            } else {
                System.out.println(args.get(1) + " exists");
            }
        }
    }
}
