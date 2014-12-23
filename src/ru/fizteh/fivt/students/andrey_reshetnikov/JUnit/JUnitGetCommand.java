package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.MultiGetCommand;

public class JUnitGetCommand extends JUnitCommand {

    String key;

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command get = new MultiGetCommand(key);
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            get.executeOnTable(base.getUsing().getDirtyTable());
        }
    }

    protected final void putArguments(String[] args) {
        key = args[1];
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }
}
