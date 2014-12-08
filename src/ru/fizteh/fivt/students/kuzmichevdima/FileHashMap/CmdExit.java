package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.io.IOException;
import java.util.Vector;

public class CmdExit implements Command {
    @Override
    public void execute(Vector<String> args, DB db) throws IOException {
        db.writeToDataBase();
        System.exit(0);
    }
}
