package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

<<<<<<< HEAD
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandCreate extends ACommand<MultiFileHashMapOperations> {
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandCreate<ATable,
        MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    public CommandCreate() {
        super("create", "create <table name>");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
        if (parameters.length > 1) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("Not enough arguments!");
        }

        Table newTable = ops.tableProvider.createTable(parameters[0]);
=======
            throw new IllegalArgumentException("too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("argument missing");
        }

        ATable newTable = ops.createTable(parameters[0]);
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
        if (newTable == null) {
            System.out.println(String.format("%s exists", parameters[0]));
        } else {
            System.out.println("created");
        }
    }
}
