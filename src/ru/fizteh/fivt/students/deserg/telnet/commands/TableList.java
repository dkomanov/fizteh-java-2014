package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deserg on 03.10.14.
 */
public class TableList implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                return "no table";
            }

            List<String> list = table.list();

            if (list.size() > 0) {

                String out = "";
                for (String key: list) {
                    out = out + key + ", ";
                }

                if (out.isEmpty()) {
                    return "";
                } else {
                    return out.substring(0, out.length() - 2);
                }
            } else {
                return "";
            }

        } else {
            return "Too many arguments";
        }

    }

}
