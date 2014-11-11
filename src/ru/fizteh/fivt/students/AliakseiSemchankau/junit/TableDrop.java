package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableDrop implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {
        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments for Drop");
        }

        Table dTable = dProvider.getTable(args.elementAt(1));

        if (dTable == null) {
            System.out.println("no such table");
            return;
        }

        dProvider.removeTable(args.elementAt(1));
    }
}
