package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiGetCommand;

public class JUnitGetCommand implements JUnitCommand {
    public JUnitGetCommand(JUnitDataBase jdb, String key) {
        this.key = key;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;

    @Override
    public void run() throws Exception {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            Command get = new MultiGetCommand(key);
            get.runOnTable(jUnitDataBase.getUsing().dirtyTable);
        }
    }
}
