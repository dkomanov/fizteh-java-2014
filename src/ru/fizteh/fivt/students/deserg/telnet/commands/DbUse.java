package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbUse implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            return "Not enough arguments";
        }
        if (args.size() == 2) {

            return db.setCurrentTable(args.get(1));

        } else {
            return "Too many arguments";
        }

    }

}
