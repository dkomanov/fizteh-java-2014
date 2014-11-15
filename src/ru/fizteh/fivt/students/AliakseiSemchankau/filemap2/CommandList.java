package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;

import java.util.*;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */
public class CommandList implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseTable dTable) {

        if (args.size() != 1) {
            throw new DatabaseException("too many arguments for list");
        }

        List<String> listOfKeys = dTable.list();

        if (listOfKeys.size() == 0) {
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