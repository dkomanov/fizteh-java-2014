package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 22.10.14.
 */
public class DbShowTables implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        if (args.size() < 2) {
            return "Wrong command";
        } else if (args.size() == 2) {

            if (!args.get(1).equals("tables")) {
                return "Wrong command";
            }

            return db.showTableSet();


        } else {
            return "Too many arguments";
        }
    }

}
