package ru.fizteh.fivt.students.deserg.filemap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandExit implements Command {

    public void execute(Vector<String> args, FileMap fileMap) {

        fileMap.write();
        System.exit(0);

    }
}
