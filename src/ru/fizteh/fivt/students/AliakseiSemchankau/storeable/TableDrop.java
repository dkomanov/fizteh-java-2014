package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

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

        String name = args.elementAt(1);

        if (dProvider.referenceToTableInfo.get(name) == null) {
            System.out.println(name + " not exists");
            return;
        }

        dProvider.removeTable(name);
        System.out.println("dropped");

    }
}
