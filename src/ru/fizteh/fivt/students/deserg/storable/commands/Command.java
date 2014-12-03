package ru.fizteh.fivt.students.deserg.storable.commands;

import ru.fizteh.fivt.students.deserg.storable.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public interface Command {

    void execute(ArrayList<String> args, DbTableProvider db);

}
