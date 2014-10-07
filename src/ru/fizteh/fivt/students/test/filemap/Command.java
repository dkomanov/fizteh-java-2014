package ru.fizteh.fivt.students.deserg.filemap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public interface Command {

    void execute(Vector<String> args, FileMap fileMap);

}
