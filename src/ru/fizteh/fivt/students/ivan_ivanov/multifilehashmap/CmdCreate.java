package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

import java.io.IOException;

public class CmdCreate implements Command {

    @Override
    public final String getName() {

        return "create";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }

        if (((MultiFileHashMap) shell).getMFHMState().createTable(args[0]) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[0] + " exists");
        }
    }
}
