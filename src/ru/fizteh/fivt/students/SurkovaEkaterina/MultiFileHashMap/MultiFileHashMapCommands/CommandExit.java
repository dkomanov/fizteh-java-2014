package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

<<<<<<< HEAD
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperations;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;

public class CommandExit extends ACommand<MultiFileHashMapOperations> {
=======
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;

public class CommandExit<ATable, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
>>>>>>> 6d17719c033094ecccc993d00c60a86a6b18d8e4
    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final MultiFileHashMapOperations ops) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("exit: Too many parameters!");
        }

        System.exit(ops.exit());
    }
}
