package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Drop;

public class JUnitDrop extends JUnitCommand {
    String tableName;

    public JUnitDrop(String passedTableName) {
        tableName = passedTableName;
    }

    public JUnitDrop() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command drop = new Drop(tableName);
        drop.startNeedInstruction(base.usualDbDir);
        base.tables.remove(tableName);
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int numberOfArguments() {
        return 1;
    }
}
