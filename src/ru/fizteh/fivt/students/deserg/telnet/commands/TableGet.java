package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.Serializer;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableGet implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            return "Not enough arguments";
        }
        if (args.size() == 2) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                return "no table";
            }

            String key = args.get(1);
            String value = Serializer.serialize(table, table.get(key));
            if (value != null) {
                return "found\n" + value;
            } else {
                return "not found";
            }

        } else {
            return "Too many arguments";
        }

    }

}
