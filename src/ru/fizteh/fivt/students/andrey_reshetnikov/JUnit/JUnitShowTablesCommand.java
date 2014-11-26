package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.ShowTablesCommand;

public class JUnitShowTablesCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command show = new ShowTablesCommand();
        show.execute(base.usualDbDir);
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
