package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 11.12.14.
 */
public class DbClose implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() == 1) {
            db.close();
            return "closed";

        } else {
            return "Too many arguments";
        }
    }

}
