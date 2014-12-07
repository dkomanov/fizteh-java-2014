package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandCreate<ATable,
        MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
    public CommandCreate() {
        super("create", "create <table name>");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("argument missing");
        }

        ATable newTable = ops.createTable(parameters[0]);
        if (newTable == null) {
            System.out.println(String.format("%s exists", parameters[0]));
        } else {
            System.out.println("created");
        }
    }
}
