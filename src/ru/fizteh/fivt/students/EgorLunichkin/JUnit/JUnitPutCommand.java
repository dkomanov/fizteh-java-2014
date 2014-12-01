package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiPutCommand;

public class JUnitPutCommand implements JUnitCommand {
    public JUnitPutCommand(JUnitDataBase jdb, String key, String value) {
        this.key = key;
        this.value = value;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;
    private String value;

    @Override
    public void run() throws Exception {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            Command put = new MultiPutCommand(key, value);
            put.runOnTable(jUnitDataBase.getUsing().dirtyTable);
            jUnitDataBase.getUsing().commands.add(put);
        }
    }
}
