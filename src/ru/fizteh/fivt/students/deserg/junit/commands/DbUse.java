package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;
import ru.fizteh.fivt.students.deserg.junit.MyException;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbUse implements Command {

    @Override
    public void execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            db.setCurrentTable(args.get(1));

        } else {
            System.out.println("Too many arguments");
        }

    }

}
