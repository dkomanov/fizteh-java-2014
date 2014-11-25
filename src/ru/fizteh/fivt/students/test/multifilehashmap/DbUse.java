package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbUse implements Command {

    @Override
    public void execute(ArrayList<String> args, Database db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {


            Table table = db.tables.get(args.get(1));
            if (table == null) {
                System.out.println("tablename does not exists");
            } else {
                db.curTable = table;
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
