package ru.fizteh.fivt.students.deserg.telnet.server.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTable;
import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 27.11.14.
 */
public class TableRollback implements DbCommand {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();

            System.out.println(table.rollback());


        } else {
            System.out.println("Too many arguments");
        }

    }

}
