package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server.DatabaseCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.StoreableUsage;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseShellOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.ACommand;

import java.io.PrintStream;

public class CommandCreate<Table, Key, Value, TableOperations
        extends DatabaseShellOperationsInterface<Table, Key, Value>>
        extends ACommand<TableOperations> {
    public CommandCreate() {
        super("create", "create <table name>");
    }

    public void executeCommand(String params, TableOperations ops, PrintStream out) {

        String tableName = StoreableUsage.parseTableName(params);

        Table newTable;
        try {
            newTable = ops.createTable(params);
        } catch (IllegalArgumentException e) {
            out.println(String.format("wrong type (%s)", e.getMessage()));
            return;
        }
        if (newTable == null) {
            out.println(String.format("%s exists", tableName));
        } else {
            out.println("created");
        }
    }
}
