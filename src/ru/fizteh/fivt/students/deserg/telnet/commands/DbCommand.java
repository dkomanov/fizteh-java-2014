package ru.fizteh.fivt.students.deserg.telnet.commands;

import ru.fizteh.fivt.students.deserg.telnet.server.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public interface DbCommand {

    String execute(ArrayList<String> args, DbTableProvider db);

}
