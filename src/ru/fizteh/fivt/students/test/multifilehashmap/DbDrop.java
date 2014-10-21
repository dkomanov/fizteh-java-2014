package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.Vector;

/**
 * Created by deserg on 22.10.14.
 */
public class DbDrop implements Command {

    @Override
    public void execute(Vector<String> args, Database db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            if (db.tables.remove(args.get(1)) == null) {
                System.out.println("tablename does not exists");
            } else {
                System.out.println("dropped");
            }

        } else {
            System.out.println("Too many arguments");
        }
    }

}
