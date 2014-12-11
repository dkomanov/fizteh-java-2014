package ru.fizteh.fivt.students.deserg.telnet.server.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableExit implements DbCommand {

    @Override
    public String execute(ArrayList<String> args, DbTableProvider db) {

        db.write();

    }
}
