package ru.fizteh.fivt.students.deserg.telnet.server.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deserg on 03.10.14.
 */
public class TableList implements DbCommand {

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
