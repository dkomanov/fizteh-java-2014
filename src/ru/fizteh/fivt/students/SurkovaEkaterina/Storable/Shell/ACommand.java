package ru.fizteh.fivt.students.SurkovaEkaterina.Storable.Shell;

public abstract class ACommand<FilesOperations>
        implements Command<FilesOperations> {

    private final String commandName;
    private final String commandParameters;

    public ACommand(final String cmdName, final String cmdParameters) {
        this.commandName = cmdName;
        this.commandParameters = cmdParameters;
    }

    public ACommand() {
        this("", "");
    }

    public final String getCommandName() {
        return commandName;
    }

    public final String getCommandParameters() {
        return commandParameters;
    }
}
