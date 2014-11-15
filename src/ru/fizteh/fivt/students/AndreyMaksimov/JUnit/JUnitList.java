package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.ListMulti;

public class JUnitList extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command list = new ListMulti();
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