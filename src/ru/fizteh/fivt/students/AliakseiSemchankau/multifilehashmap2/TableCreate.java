package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap2;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 14.11.2014.
 */

public class TableCreate implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 2) {
            throw new DatabaseException("wrong number of args for create");
        }

        String name = args.elementAt(1);

        if (dProvider.referenceToTableInfo.get(name) != null) {
            System.out.println(name + "exists");
            return;
        }

        dProvider.createTable(name);
        System.out.println("created");

    }
}