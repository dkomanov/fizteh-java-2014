package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public interface Command {

    void execute(Vector<String> args, Database db);

}
