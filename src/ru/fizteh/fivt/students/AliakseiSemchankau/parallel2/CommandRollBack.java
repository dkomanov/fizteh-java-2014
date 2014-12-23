package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;


import ru.fizteh.fivt.storage.structured.Table;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandRollBack implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 1) {
            throw new DatabaseException("incorrect number for arguments(commit)");
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

        int changesDeleted = dTable.rollback();
        System.out.println(changesDeleted);
    }

}
