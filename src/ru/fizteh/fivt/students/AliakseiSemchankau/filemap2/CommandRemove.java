package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;


import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandRemove implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseTable dTable) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number for arguments(remove)");
        }

        String key = args.elementAt(1);

        if (!dTable.dbMap.containsKey(key)) {
            System.out.println("not found");
            return;
        }

        dTable.remove(key);
        System.out.println("removed");
    }

}