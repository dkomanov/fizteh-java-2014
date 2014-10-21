package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class TableList implements Command {

    @Override
    public void execute(Vector<String> args, Database db) {

        if (args.size() == 1) {


            if (db.curTable == null) {
                System.out.println("no table");
                return;
            }

            if (db.curTable.size() > 0) {

                Iterator<String> it = db.curTable.keySet().iterator();
                System.out.print(it.next());
                while (it.hasNext()) {
                    System.out.print(", " + it.next());
                }

            }

            System.out.println("");


        } else {
            System.out.println("Too many arguments");
        }

    }

}
