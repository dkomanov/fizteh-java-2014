package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.IOException;

public class CommandMkdir extends ACommand<FilesOperations> {

    public CommandMkdir() {
        super("mkdir", "mkdir <dirname>");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws IOException {
        if (CommandsParser.getParametersNumber(parameters) > 1) {
            throw new IllegalArgumentException("mkdir: too many arguments!");
        } else if (CommandsParser.getParametersNumber(parameters) < 1) {
            throw new IllegalArgumentException("mkdir: not eniugh arguments!");
        }
        operations.makeNewDirectory(parameters);
    }
}
