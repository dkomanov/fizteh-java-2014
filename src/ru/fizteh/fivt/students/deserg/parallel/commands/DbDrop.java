package ru.fizteh.fivt.students.deserg.parallel.commands;

import ru.fizteh.fivt.students.deserg.parallel.DbTableProvider;
import ru.fizteh.fivt.students.deserg.parallel.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbDrop implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            try {
                db.removeTable(args.get(1));
                System.out.println("dropped");
            } catch (IllegalStateException ex) {
                System.out.println("table does not exists");
            }

        } else {
            System.out.println("Too many arguments");
        }
    }

}
