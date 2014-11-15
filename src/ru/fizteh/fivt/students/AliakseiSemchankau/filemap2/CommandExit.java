 package ru.fizteh.fivt.students.AliakseiSemchankau.filemap2;

 import java.util.Vector;

 /**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandExit implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseTable dTable) {

        if (args.size() != 1) {
            throw new DatabaseException("incorrect number of arguments(get)");
        }

        dTable.writeFile(dTable.pathToTable);

    }

}
