package ru.fizteh.fivt.students.AliakseiSemchankau.parallel2;


import java.util.*;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class CommandList implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 1) {
            throw new DatabaseException("too many arguments for list");
        }

        if (dProvider.currentTableName == null) {
            System.out.println("choose a table at first(list)");
            return;
        }

        DatabaseTable dTable = dProvider.referenceToTableInfo.get(dProvider.currentTableName);

        if (dTable == null) {
            System.out.println("choose a table at first(list)");
            return;
        }

        List<String> listOfKeys = dTable.list();

        if (listOfKeys == null) {
            System.out.println("");
            return;
        }

        if (listOfKeys.size() == 0) {
            //System.out.println("it is empty((");
            System.out.println("");
            return;
        }

        int curSize = 0;

        for (String currentKey : listOfKeys) {
            ++curSize;
            if (curSize < listOfKeys.size()) {
                System.out.print(currentKey);
                System.out.print(", ");
            } else {
                System.out.print(currentKey);
            }
        }
        System.out.println("");


    }
}
