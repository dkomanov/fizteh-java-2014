package ru.fizteh.fivt.students.SurkovaEkaterina.shell;

public class CommandPwd extends ACommand<FilesOperations> {
    public CommandPwd() {
        super("pwd", "pwd");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations) {
        if (!parameters.equals("")) {
            throw new IllegalArgumentException("pwd: To many arguments!");
        }
        String currentDirectory = operations.getCurrentDirectory();
        System.out.println(currentDirectory);
    }
}
