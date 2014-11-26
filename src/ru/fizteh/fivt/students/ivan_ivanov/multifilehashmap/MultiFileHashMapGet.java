package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

import java.io.IOException;

public class MultiFileHashMapGet implements Command {

    @Override
    public final String getName() {

        return "get";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("incorrect number of arguments");
            return;
        }
        if (((MultiFileHashMap) shell).getMFHMState().getCurrentTable() == null) {
            System.out.println("no table");
            return;
        }
        String value = ((MultiFileHashMap) shell).getMFHMState().getFromCurrentTable(args[0]);
        if (null == value) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
