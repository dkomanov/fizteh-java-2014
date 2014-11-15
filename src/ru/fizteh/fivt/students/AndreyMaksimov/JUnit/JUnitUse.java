package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Use;

public class JUnitUse extends JUnitCommand {

    String tableName;

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() != null && base.getUsing().changes.size() != 0) {
            System.out.println(base.getUsing().changes.size() + " unsaved changes");
        } else {
            Command use = new Use(tableName);
            use.startNeedInstruction(base.usualDbDir);
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