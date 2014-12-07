package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

<<<<<<< HEAD
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandDrop extends ACommand<MultiFileHashMapOperations> {
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandDrop<ATable, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    public CommandDrop() {
        super("drop", "drop <table name>");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("Not enough arguments!");
        }

        try {
            ops.dropTable(parameters[0]);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
