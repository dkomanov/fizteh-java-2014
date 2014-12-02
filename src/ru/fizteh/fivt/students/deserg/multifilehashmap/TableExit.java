package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class TableExit implements Command {

    @Override
    public void execute(ArrayList<String> args, Database db) {

        db.write();
        System.exit(0);

    }
}
