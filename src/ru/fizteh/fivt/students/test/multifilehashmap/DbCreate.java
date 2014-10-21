package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.nio.file.Path;
import java.util.Vector;

/**
 * Created by deserg on 22.10.14.
 */
public class DbCreate implements Command {

    @Override
    public void execute(Vector<String> args, Database db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            String tableName = args.get(1);
            Path tablePath = db.getDbPath().resolve(tableName);


            if (db.tables.put(tableName, new Table(tablePath)) == null) {
                System.out.println("created");
            } else {
                System.out.println("tablename exists");
            }

        } else {
            System.out.println("Too many arguments");
        }
    }
}
