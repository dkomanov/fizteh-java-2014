package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.ShowTables;

public class JUnitShow extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command show = new ShowTables();
        show.startNeedInstruction(base.usualDbDir);
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
