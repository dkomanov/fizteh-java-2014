package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.IOException;

/**
 * Created by Kirill on 23.09.2014.
 */
public interface Command {
    String toString();

    int execute(String[] args) throws IOException;
}
