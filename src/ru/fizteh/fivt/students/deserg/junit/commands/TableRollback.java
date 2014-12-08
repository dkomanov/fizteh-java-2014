package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 27.11.14.
 */
public class TableRollback implements Command {

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
