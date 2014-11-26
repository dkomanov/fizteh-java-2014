package ru.fizteh.fivt.students.SurkovaEkaterina.shell;

public class CommandLs extends ACommand<FilesOperations> {
    public CommandLs() {
        super("ls", "ls");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations) {
        if (!parameters.equals("")) {
            throw new IllegalArgumentException("ls: To many arguments!");
        }
        operations.printDirectory();
    }
}
