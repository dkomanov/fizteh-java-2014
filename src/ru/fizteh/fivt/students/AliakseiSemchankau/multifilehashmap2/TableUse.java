 package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap2;

/**
 * Created by Aliaksei Semchankau on 14.11.2014.
 */
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableUse implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments for use");
        }

        String name = args.elementAt(1);

        if (dProvider.referenceToTableInfo.get(name) == null) {
            System.out.println("no such table");
        }

        if (dProvider.referenceToTableInfo.get(name) == null) {
            System.out.println(name + " not exists");
            return;
        }

        DatabaseTable dTable = dProvider.getTable(args.elementAt(1));

        if (dTable == null) {
            System.out.println(name + " not exists");
            return;
        }

        System.out.println("using " + name);

    }
}
