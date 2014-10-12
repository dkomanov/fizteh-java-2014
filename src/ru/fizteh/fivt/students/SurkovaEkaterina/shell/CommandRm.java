package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.File;

public class CommandRm extends ACommand<FilesOperations> {
    public CommandRm() {
        super("rm", "rm [-r] <file|dir> ");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations) {
        String[] parsArray = CommandsParser.parseCommandParameters(parameters);
        if (parsArray.length > 2) {
            throw new IllegalArgumentException("rm: Too many arguments!");
        } else if (parsArray.length < 1) {
            throw new IllegalArgumentException("rm: Not enough arguments!");
        }
        if (parsArray.length == 2) {
            String filePath = operations.getPath(parsArray[1]).toString();
            File file = operations.getPath(parsArray[1]).toFile();
            if (!parsArray[0].equals("-r")) {
                throw new IllegalArgumentException("rm: Invalid arguments!");
            }
            if (!file.isDirectory()) {
                throw new IllegalArgumentException(
                        "rm: " + parsArray[1] + ": is not a directory");
            }
            operations.removeFile(parsArray[1]);
        } else {
            String filePath = operations.getPath(parsArray[0]).toString();
            File file = operations.getPath(parsArray[0]).toFile();
            if (file.isDirectory()) {
                throw new IllegalArgumentException(
                        "rm: " + parsArray[0] + ": is a directory");
            }
            operations.removeFile(parsArray[0]);
        }
    }
}
