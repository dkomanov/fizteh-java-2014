package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandUse<Table, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<Table>>
        extends ACommand<MultiFileHashMapOperations> {
    public CommandUse() {
        super("use", "use <table name>");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        Table newTable;
        if (parameters.length > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("Not enough arguments!");
        }
        try {
            newTable = ops.useTable(parameters[0]);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s not exists", parameters[0]));
            return;
        }

        System.out.println(String.format("using %s", ops.getTableName()));
    }
}

