package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class TablePut implements Command {

    @Override
    public void execute(Vector<String> args, Database db) {

        if (args.size() < 3) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 3) {

            if (db.curTable == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);
            String value = args.get(2);

            if (db.curTable.put(key, value) != null) {
                System.out.println("overwrite");
            } else {
                System.out.println("new");

            }

        } else {
            System.out.println("Too many arguments");
        }

    }
}
