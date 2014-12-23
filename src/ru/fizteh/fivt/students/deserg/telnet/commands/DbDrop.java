package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbDrop implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            return "Not enough arguments";
        }
        if (args.size() == 2) {

            try {
                db.removeTable(args.get(1));
                return "dropped";
            } catch (IllegalStateException ex) {
                return "table does not exists";
            }

        } else {
            return "Too many arguments";
        }
    }

}
