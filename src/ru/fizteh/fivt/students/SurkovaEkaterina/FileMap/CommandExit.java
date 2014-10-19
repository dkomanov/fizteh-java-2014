package ru.fizteh.fivt.students.SurkovaEkaterina.FileMap;

import ru.fizteh.fivt.students.SurkovaEkaterina.shell.ACommand;

public class CommandExit<FileMapShellOperations
        extends FileMapShellOperationsInterface>
        extends ACommand<FileMapShellOperations> {
    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                               final FileMapShellOperations operations) {
        if (!parameters.isEmpty()) {
            throw new IllegalArgumentException("exit: Too many parameters!");
        }

        System.exit(0);
    }
}
