package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;
import ru.fizteh.fivt.students.deserg.junit.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TablePut implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 3) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 3) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);
            String value = args.get(2);

            if (table.put(key, value) != null) {
                System.out.println("overwrite");
            } else {
                System.out.println("new");

            }

        } else {
            System.out.println("Too many arguments");
        }

    }
}
