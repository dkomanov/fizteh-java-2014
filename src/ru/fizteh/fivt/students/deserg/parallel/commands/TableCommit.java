package ru.fizteh.fivt.students.deserg.parallel.commands;

import ru.fizteh.fivt.students.deserg.parallel.DbTable;
import ru.fizteh.fivt.students.deserg.parallel.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 27.11.14.
 */
public class TableCommit implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {

            DbTable table = db.getCurrentTable();

            System.out.println(table.commit());


        } else {
            System.out.println("Too many arguments");
        }

    }

}
