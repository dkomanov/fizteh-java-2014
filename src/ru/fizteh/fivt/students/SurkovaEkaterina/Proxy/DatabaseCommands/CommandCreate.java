package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.StoreableUsage;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Shell.ACommand;

public class CommandCreate<Table, Key, Value, TableOperations
        extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandCreate() {
        super("create", "create <table name>");
    }

    public void executeCommand(String params, TableOperations ops) {

        String tableName = StoreableUsage.parseTableName(params);

        Table newTable;
        try {
            newTable = ops.createTable(params);
        } catch (IllegalArgumentException e) {
            System.out.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        }
        if (newTable == null) {
            System.out.println(String.format("%s exists", tableName));
        } else {
            System.out.println("created");
        }
    }
}
