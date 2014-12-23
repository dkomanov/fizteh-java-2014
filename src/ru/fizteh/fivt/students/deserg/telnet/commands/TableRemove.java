package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;
import ru.fizteh.fivt.students.deserg.telnet.exceptions.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableRemove implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            DbTable table = db.getCurrentTable();

            if (table == null) {
                return "no table";
            }

            String key = args.get(1);

            if (table.remove(key) == null) {
                return "not found";
            } else {
                return "removed";
            }

        } else {
            return "Too many arguments";
        }

    }

}
