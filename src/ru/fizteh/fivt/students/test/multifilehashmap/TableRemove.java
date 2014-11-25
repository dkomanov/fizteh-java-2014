package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableRemove implements Command {

    @Override
    public void execute(ArrayList<String> args, Database db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            if (db.curTable == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);
            String value = db.curTable.remove(key);

            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
