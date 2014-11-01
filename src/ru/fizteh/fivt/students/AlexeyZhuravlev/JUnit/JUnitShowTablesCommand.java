package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ShowTablesCommand;

/**
 * @author AlexeyZhuravlev
 */
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
