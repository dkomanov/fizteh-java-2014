package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public interface Command {

    void execute(ArrayList<String> args, Database db);

}
