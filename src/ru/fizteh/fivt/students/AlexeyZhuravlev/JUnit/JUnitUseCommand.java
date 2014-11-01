package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.UseCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitUseCommand extends JUnitCommand {

    String tableName;

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() != null && base.getUsing().changes.size() != 0) {
            System.out.println(base.getUsing().changes.size() + " unsaved changes");
        } else {
            Command use = new UseCommand(tableName);
            use.execute(base.usualDbDir);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        tableName = args[1];
    }
}
