package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.MultiGetCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitGetCommand extends JUnitCommand {

    String key;

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command get = new MultiGetCommand(key);
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            get.executeOnTable(base.getUsing().dirtyTable);
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
