package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.IOException;

public class CommandMv extends ACommand<FilesOperations> {
    CommandMv() {
        super("mv", "mv <source> <destination>");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws IOException {
        int parametersNumber = CommandsParser.getParametersNumber(parameters);
        String[] parsArray = CommandsParser.parseCommandParameters(parameters);
        if (parametersNumber > 2) {
            throw new IllegalArgumentException("mv: too many parameters!");
        } else if (parametersNumber < 2) {
            throw new IllegalArgumentException("mv: not enough arguments!");
        }
        operations.moveFile(parsArray[0], parsArray[1]);
    }
}
