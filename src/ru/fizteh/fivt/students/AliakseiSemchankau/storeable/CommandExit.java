package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 13.10.2014.
 */


public class CommandExit implements CommandInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {
        if (args.size() != 1) {
            throw new DatabaseException("wrong number of arguments for exit");
        }

    }
}