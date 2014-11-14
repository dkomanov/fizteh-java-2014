 package ru.fizteh.fivt.students.AliakseiSemchankau.multifilehashmap2;

 import java.util.Map;
 import java.util.Vector;

 /**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */

public class CommandExit implements CommandInterface {

    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        if (args.size() != 1) {
            throw new DatabaseException("incorrect number of arguments(get)");
        }

        for (Map.Entry<String, DatabaseTable> entry : dProvider.referenceToTableInfo.entrySet()) {
            entry.getValue().writeTable();
        }

    }

}