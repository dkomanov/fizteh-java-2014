package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Executor;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;

public class FileMapExecutor extends Executor {
    public FileMapExecutor() {
        list();
    }
    public final void list() {
        Command put = new Put();
        mapOfCmd.put(put.getName(), put);
        Command get = new Get();
        mapOfCmd.put(get.getName(), get);
        Command remove = new Remove();
        mapOfCmd.put(remove.getName(), remove);
        Command list = new List();
        mapOfCmd.put(list.getName(), list);
        Command exit = new Exit();
        mapOfCmd.put(exit.getName(), exit);
    }
}
