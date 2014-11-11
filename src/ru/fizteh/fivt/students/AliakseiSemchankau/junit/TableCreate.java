package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
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
