package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;


import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.DropCommand;

public class JUnitDropCommand extends JUnitCommand {
    String tableName;

    public JUnitDropCommand(String passedTableName) {
        tableName = passedTableName;
    }

    public JUnitDropCommand() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command drop = new DropCommand(tableName);
        drop.execute(base.usualDbDir);
        base.tables.remove(tableName);
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int numberOfArguments() {
        return 1;
    }
}
