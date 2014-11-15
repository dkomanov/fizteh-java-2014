package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Create;

public class JUnitCreate extends JUnitCommand {
    private String tableName;

    public JUnitCreate(String passedTableName) {
        tableName = passedTableName;
    }

    public JUnitCreate() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command command = new Create(tableName);
        command.startNeedInstruction(base.usualDbDir);
        base.tables.put(tableName, new HybridTable(base.usualDbDir.tables.get(tableName)));
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int numberOfArguments() {
        return 1;
    }
}