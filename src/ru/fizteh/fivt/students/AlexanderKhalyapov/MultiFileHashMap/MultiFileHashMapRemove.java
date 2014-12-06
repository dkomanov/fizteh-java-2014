package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;

public class MultiFileHashMapRemove implements Command {
    @Override
    public final String getName() {
        return "remove";
    }
    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = ((MultiFileHashMap) shell).getMFHMState().removeFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
