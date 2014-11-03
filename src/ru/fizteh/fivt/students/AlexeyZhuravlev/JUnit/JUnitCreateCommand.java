package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.CreateCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitCreateCommand extends JUnitCommand {
    private String tableName;

    public JUnitCreateCommand(String passedTableName) {
        tableName = passedTableName;
    }

    public JUnitCreateCommand() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command command = new CreateCommand(tableName);
        command.execute(base.usualDbDir);
        base.tables.put(tableName, new HybridTable(base.usualDbDir.tables.get(tableName)));
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int numberOfArguments() {
        return 1;
    }
}
