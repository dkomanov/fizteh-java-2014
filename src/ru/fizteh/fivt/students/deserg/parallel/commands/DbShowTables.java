package ru.fizteh.fivt.students.deserg.parallel.commands;

import ru.fizteh.fivt.students.deserg.parallel.DbTableProvider;
import ru.fizteh.fivt.students.deserg.parallel.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbShowTables implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Wrong command");
        }
        if (args.size() == 2) {

            if (!args.get(1).equals("tables")) {
                throw new MyException("Wrong command");
            }

            db.showTableSet();


        } else {
            System.out.println("Too many arguments");
        }
    }

}
