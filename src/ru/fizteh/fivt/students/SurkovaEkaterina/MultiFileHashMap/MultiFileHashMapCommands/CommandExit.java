package ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapCommands;

import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.MultiFileHashMapOperationsInterface;
import ru.fizteh.fivt.students.SurkovaEkaterina.MultiFileHashMap.Shell.ACommand;

public class CommandExit<ATable, MultiFileHashMapOperations
        extends MultiFileHashMapOperationsInterface<ATable>>
        extends ACommand<MultiFileHashMapOperations> {
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
