package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.io.IOException;
import java.util.Vector;

public interface Command {
    void execute(Vector<String> args, DB db) throws IOException;
}
