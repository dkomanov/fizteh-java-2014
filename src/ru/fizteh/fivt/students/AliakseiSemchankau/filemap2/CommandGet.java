package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;


import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandGet implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseTable dTable) {

        if (args.size() != 2) {
            throw new DatabaseException("incorrect number of arguments(get)");
        }

        String key = args.elementAt(1);
        String value = dTable.get(key);

        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }

}
