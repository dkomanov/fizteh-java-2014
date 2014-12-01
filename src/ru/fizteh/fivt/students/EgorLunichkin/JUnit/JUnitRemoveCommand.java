package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

public class JUnitRemoveCommand implements Command {
    public JUnitRemoveCommand(JUnitDataBase jdb, String key) {
        this.key = key;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;

    public void run() {

    }

    public void runOnTable(Table table) {

    }
}
