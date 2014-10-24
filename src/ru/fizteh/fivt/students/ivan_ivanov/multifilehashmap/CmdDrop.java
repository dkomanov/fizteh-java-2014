package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

import java.io.IOException;

public class CmdDrop implements Command {

    @Override
    public final String getName() {

        return "drop";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        if (((MultiFileHashMap) shell).getMFHMState().getTable(args[0]) == null) {
            System.out.println(args[0] + " not exists");
            return;
        } else {
            ((MultiFileHashMap) shell).getMFHMState().deleteTable(args[0]);
            System.out.println("dropped");
        }
    }
}
