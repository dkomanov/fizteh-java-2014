package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import ru.fizteh.fivt.storage.structured.Table;

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

        if (dProvider.currentTableName != null) {
            int unsavedChanges = dProvider.referenceToTableInfo.get(dProvider.currentTableName).unsavedChanges;
            if (unsavedChanges > 0) {
                System.out.println(unsavedChanges + " unsaved changes");
                return;
            }
        }

        if (dProvider.referenceToTableInfo.get(name) == null) {
            System.out.println(name + " not exists");
            return;
        }

        Table dTable = dProvider.getTable(args.elementAt(1));

        if (dTable == null) {
            System.out.println(name + " not exists");
            return;
        }

        System.out.println("using " + name);

    }
}