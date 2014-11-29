package ru.fizteh.fivt.students.VasilevKirill.proxy.parallel.Commands.shelldata;

import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public interface Command {
    String toString();

    int execute(String[] args, Status status) throws IOException;

    boolean checkArgs(String[] args);
}
