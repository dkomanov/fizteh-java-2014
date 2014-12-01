package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

public class JUnitShowTablesCommand implements Command {
    public JUnitShowTablesCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() {

    }

    public void runOnTable(Table table) {

    }
}
