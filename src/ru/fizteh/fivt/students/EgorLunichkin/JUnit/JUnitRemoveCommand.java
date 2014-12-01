package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiRemoveCommand;

public class JUnitRemoveCommand implements JUnitCommand {
    public JUnitRemoveCommand(JUnitDataBase jdb, String key) {
        this.key = key;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;

    public void run() throws Exception {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            Command remove = new MultiRemoveCommand(key);
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            if (jUnitDataBase.getUsing().dirtyTable.dataBases[dir][file].getDataBase().get(key) != null) {
                remove.runOnTable(jUnitDataBase.getUsing().dirtyTable);
                jUnitDataBase.getUsing().commands.add(remove);
            } else {
                System.out.println("not found");
            }
        }
    }
}
