package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.DropCommand;

public class JUnitDropCommand implements JUnitCommand {
    public JUnitDropCommand(JUnitDataBase jdb, String name) {
        this.tableName = name;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String tableName;

    public void run() throws Exception {
        Command drop = new DropCommand(jUnitDataBase.multiDataBase, tableName);
        drop.run();
        jUnitDataBase.tables.remove(tableName);
    }
}
