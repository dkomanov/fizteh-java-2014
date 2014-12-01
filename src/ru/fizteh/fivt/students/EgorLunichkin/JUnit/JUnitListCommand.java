package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiListCommand;

public class JUnitListCommand implements JUnitCommand {
    public JUnitListCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() throws Exception {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            Command list = new MultiListCommand();
            list.runOnTable(jUnitDataBase.getUsing().dirtyTable);
        }
    }
}
