package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 27.11.14.
 */
public class TableCommit implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();

            if (table == null) {
                return "no table";
            }

            return String.valueOf(table.commit());

        } else {
            return "Too many arguments";
        }

    }

}
