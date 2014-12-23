package ru.fizteh.fivt.students.deserg.proxy.commands;

import ru.fizteh.fivt.students.deserg.proxy.DbTable;
import ru.fizteh.fivt.students.deserg.proxy.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 27.11.14.
 */

public class TableSize implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();

            System.out.println(table.size());


        } else {
            System.out.println("Too many arguments");
        }

    }

}
