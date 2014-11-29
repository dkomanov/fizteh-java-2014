package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.deserg.junit.DbTable;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;
import ru.fizteh.fivt.students.deserg.junit.MyException;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbCreate implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            String tableName = args.get(1);

            if (db.createTable(tableName) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }

        } else {
            System.out.println("Too many arguments");
        }
    }
}
