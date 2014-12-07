package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableList implements Command {

    @Override
    public void execute(ArrayList<String> args, Database db) {

        if (args.size() == 1) {


            if (db.curTable == null) {
                System.out.println("no table");
                return;
            }

            if (db.curTable.size() > 0) {

                String out = "";
                for (String key: db.curTable.keySet()) {
                    out = out + key + ", ";
                }

                if (out.isEmpty()) {
                    System.out.println("");
                } else {
                    System.out.println(out.substring(0, out.length() - 2));
                }

            }

            System.out.println("");


        } else {
            System.out.println("Too many arguments");
        }

    }

}
