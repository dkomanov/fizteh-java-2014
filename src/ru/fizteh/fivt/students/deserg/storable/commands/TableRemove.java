package ru.fizteh.fivt.students.deserg.storable.commands;

import ru.fizteh.fivt.students.deserg.storable.DbTable;
import ru.fizteh.fivt.students.deserg.storable.DbTableProvider;
import ru.fizteh.fivt.students.deserg.storable.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableRemove implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);

            if (table.remove(key) == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
