package ru.fizteh.fivt.students.deserg.parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.deserg.parallel.DbTable;
import ru.fizteh.fivt.students.deserg.parallel.DbTableProvider;
import ru.fizteh.fivt.students.deserg.parallel.MyException;
import ru.fizteh.fivt.students.deserg.parallel.Serializer;

import java.text.ParseException;
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
        if (args.size() >= 3) {

            DbTable table = db.getCurrentTable();
            if (table == null) {
                System.out.println("no table");
                return;
            }

            String key = args.get(1);
            String value = "";
            for (int i = 2; i < args.size(); i++) {
                value += args.get(i);
            }

            Storeable mValue;

            try {
                mValue = Serializer.deserialize(table, value);
            } catch (ParseException ex) {
                throw new MyException("wrong type " + ex.getMessage());
            }

            if (table.put(key, mValue) != null) {
                System.out.println("overwrite");
            } else {
                System.out.println("new");

            }

        }

    }
}
