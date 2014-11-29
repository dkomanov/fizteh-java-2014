package ru.fizteh.fivt.students.deserg.junit.commands;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.deserg.junit.DbTableProvider;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public interface Command {

    void execute(ArrayList<String> args, DbTableProvider db);

}
