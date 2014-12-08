package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;


import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandCommit implements CommandInterface {

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

        try {
            int changesSaved = dTable.commit();
            System.out.println(changesSaved);
        } catch (IOException ioexc) {
            throw new DatabaseException("can't commit");
        }
    }

}
