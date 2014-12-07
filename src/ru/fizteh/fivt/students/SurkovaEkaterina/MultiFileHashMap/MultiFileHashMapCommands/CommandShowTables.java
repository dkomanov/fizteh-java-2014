package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

<<<<<<< HEAD
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandShowTables extends ACommand<MultiFileHashMapOperations> {
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandShowTables<ATable, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    public CommandShowTables() {
        super("show", "show tables");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException(
                    "show tables: Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException(
                    "Unknown command: show");
        }
        if (!(parameters[0].equals("tables"))) {
            throw new IllegalArgumentException(
                    "Unknown command: show " + params);
        }

        ops.showTables();
    }
}
