package ru.fizteh.fivt.students.AliakseiSemchankau.junit;

import ru.fizteh.fivt.storage.strings.Table;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandRemove implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number for arguments(remove)");
        }

        if (dProvider.currentTableName == null) {
            System.out.println("choose a table at first");
            return;
        }

        Table dTable = dProvider.getTable(dProvider.currentTableName);

        if (dTable == null) {
            System.out.println("choose a table at first");
            return;
        }

        String key = args.elementAt(1);
        String value = dTable.get(key);

        if (value == null) {
            System.out.println("not found");
            return;
        }

        dTable.remove(key);
        System.out.println("removed");
    }

}
