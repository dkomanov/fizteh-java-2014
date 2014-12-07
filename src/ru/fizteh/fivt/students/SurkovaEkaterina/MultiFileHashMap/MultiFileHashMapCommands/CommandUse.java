package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

<<<<<<< HEAD
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.FileMap.ATable;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandUse extends ACommand<MultiFileHashMapOperations> {
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.CommandsParser;

public class CommandUse<Table, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<Table>>
        extends ACommand<MultiFileHashMapOperations> {
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    public CommandUse() {
        super("use", "use <table name>");
    }

    public final void executeCommand(final String params,
                                     final MultiFileHashMapOperations ops) {
        String[] parameters = CommandsParser.parseCommandParameters(params);
<<<<<<< HEAD
        ATable newTable;
=======
        Table newTable;
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
        if (parameters.length > 1) {
            throw new IllegalArgumentException("Too many arguments!");
        }
        if (parameters.length < 1) {
            throw new IllegalArgumentException("Not enough arguments!");
        }
        try {
<<<<<<< HEAD
            newTable = ops.getTable(parameters[0]);
=======
            newTable = ops.useTable(parameters[0]);
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (newTable == null) {
            System.out.println(String.format("%s not exists", parameters[0]));
            return;
        }

<<<<<<< HEAD
        if (ops.table != null) {
            ops.table.commit();
        }
        ops.table = newTable;
        System.out.println(String.format("using %s", ops.table.getName()));
=======
        System.out.println(String.format("using %s", ops.getTableName()));
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    }
}

