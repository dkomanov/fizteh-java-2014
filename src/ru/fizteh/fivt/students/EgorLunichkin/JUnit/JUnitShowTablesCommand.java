package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.ShowTablesCommand;

public class JUnitShowTablesCommand implements JUnitCommand {
    public JUnitShowTablesCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() throws Exception {
        Command showTables = new ShowTablesCommand(jUnitDataBase.multiDataBase);
        showTables.run();
    }
}
