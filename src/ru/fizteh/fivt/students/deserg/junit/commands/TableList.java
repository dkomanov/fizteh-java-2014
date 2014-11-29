package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by deserg on 03.10.14.
 */
public class TableList implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                System.out.println("no table");
                return;
            }

            List<String> list = table.list();

            if (list.size() > 0) {

                String out = "";
                for (String key: list) {
                    out = out + key + ", ";
                }

                if (out.isEmpty()) {
                    System.out.println("");
                } else {
                    System.out.println(out.substring(0, out.length() - 2));
                }

            }

            System.out.println();


        } else {
            System.out.println("Too many arguments");
        }

    }

}
