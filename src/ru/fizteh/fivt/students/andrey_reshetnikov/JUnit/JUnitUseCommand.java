package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.UseCommand;

public class JUnitUseCommand extends JUnitCommand {

    protected String tableName;

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
