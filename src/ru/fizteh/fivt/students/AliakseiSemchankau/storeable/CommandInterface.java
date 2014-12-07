package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import java.util.Vector;

public interface CommandInterface {

    void makeCommand(Vector<String> args, DatabaseProvider dProvider);

}
