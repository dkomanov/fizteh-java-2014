package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.MultiListCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitListCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command list = new MultiListCommand();
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            list.executeOnTable(base.getUsing().dirtyTable);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
