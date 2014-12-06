package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;
import java.util.Set;

public class MultiFileHashMapList implements Command {
    @Override
    public final String getName() {
        return "list";
    }
    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 0) {
            System.out.println("Incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        Set<String> keys = ((MultiFileHashMap) shell).getMFHMState().getDataBaseFromCurrentTable().keySet();
        System.out.println(String.join(", ", keys));
    }
}
