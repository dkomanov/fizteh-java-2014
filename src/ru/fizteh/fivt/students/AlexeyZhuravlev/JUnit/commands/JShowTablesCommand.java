package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ShowTablesCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JShowTablesCommand extends JCommand {
    @Override
    public void execute(MyTableProvider base) throws Exception {
        Command show = new ShowTablesCommand();
        show.execute(base.getUsual());
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
