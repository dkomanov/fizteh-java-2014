package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.DropCommand;

/**
 * @author AlexeyZhuravlev
 */
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
