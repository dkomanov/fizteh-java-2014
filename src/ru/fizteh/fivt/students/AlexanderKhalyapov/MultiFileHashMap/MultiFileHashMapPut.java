package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;

public class MultiFileHashMapPut implements Command {
    @Override
    public final String getName() {
        return "put";
    }
    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = ((MultiFileHashMap) shell).getMFHMState().putToCurrentTable(args[0], args[1]);
        if (null == value) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }
}
