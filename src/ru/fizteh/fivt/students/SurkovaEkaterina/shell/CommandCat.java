package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.FileNotFoundException;

public class CommandCat extends ACommand<FilesOperations> {
    public CommandCat() {
        super("cat", "cat <file>");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws FileNotFoundException {
        if (CommandsParser.getParametersNumber(parameters) > 1) {
            throw new IllegalArgumentException("cat: too many parameters!");
        } else if (CommandsParser.getParametersNumber(parameters) < 1) {
            throw new IllegalArgumentException("cat: file name is needed!");
        }
        operations.printFile(parameters);
    }
}
