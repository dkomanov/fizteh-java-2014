package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;

public class CmdCreate implements Command {
    @Override
    public final String getName() {
        return "create";
    }
    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().createTable(args[0]) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[0] + " exists");
        }
    }
}
