package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.Serializer;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableDescribe implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            return "Not enough arguments";


        } else if (args.size() == 2) {

            DbTable table = db.getCurrentTable();

            if (table == null) {
                return "no table";
            }


            return Serializer.makeStringFromSignature(table.getSignature());

        } else {
            return "Too many arguments";
        }

    }
}
