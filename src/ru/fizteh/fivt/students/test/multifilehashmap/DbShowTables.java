package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbShowTables implements Command {

    @Override
    public void execute(ArrayList<String> args, Database db) {

        if (args.size() < 2) {
            throw new MyException("Wrong command");
        }
        if (args.size() == 2) {

            if (!args.get(1).equals("tables")) {
                throw new MyException("Wrong command");
            }

            System.out.println("table_name row_count");

            for (HashMap.Entry<String, Table> entry: db.tables.entrySet()) {

                System.out.println(entry.getKey() + " " + entry.getValue().size());

            }

        } else {
            System.out.println("Too many arguments");
        }
    }

}
