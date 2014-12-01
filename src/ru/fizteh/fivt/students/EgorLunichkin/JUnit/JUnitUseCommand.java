package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.UseCommand;

public class JUnitUseCommand implements JUnitCommand {
    public JUnitUseCommand(JUnitDataBase jdb, String name) {
        this.tableName = name;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String tableName;

    public void run() throws Exception {
        if (jUnitDataBase.getUsing() != null && jUnitDataBase.getUsing().commands.size() > 0) {
            System.out.println(jUnitDataBase.getUsing().commands.size() + " unsaved changes");
        } else {
            Command use = new UseCommand(jUnitDataBase.multiDataBase, tableName);
            use.run();
        }
    }
}
