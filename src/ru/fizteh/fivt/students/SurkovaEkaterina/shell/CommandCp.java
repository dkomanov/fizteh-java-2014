package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.File;
import java.io.IOException;

public class CommandCp extends ACommand<FilesOperations> {
    private final int allowableParamsNumber = 3;

    public CommandCp() {
        super("cp", "cp [-r] <source> <destination>");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws IOException {
        String[] parametersArray =
                CommandsParser.parseCommandParameters(parameters);
        if (parametersArray.length > allowableParamsNumber) {
            throw new IllegalArgumentException("cp: Too many arguments!");
        } else if (parametersArray.length < allowableParamsNumber - 1) {
            throw new IllegalArgumentException("cp: Not enough arguments!");
        }
        if (parametersArray.length == allowableParamsNumber) {
            File sourceFile = operations.getPath(parametersArray[1]).toFile();
            File destinationFile =
                    operations.getPath(parametersArray[2]).toFile();
            if (!parametersArray[0].equals("-r")) {
                throw new IllegalArgumentException("cp: Invalid arguments!");
            }
            if (!sourceFile.isDirectory()) {
                throw new IllegalArgumentException(
                        "cp: " + parametersArray[1] + " is not a directory.");
            }
            operations.copyFile(parametersArray[1], parametersArray[2]);
        } else {
            File sourceFile = operations.getPath(parametersArray[0]).toFile();
            File destinationFile =
                    operations.getPath(parametersArray[1]).toFile();
            if (sourceFile.isDirectory()) {
                throw new IllegalArgumentException(
                        "cp: " + parametersArray[0] + " is a directory (not copied).");
            }
            operations.copyFile(parametersArray[0], parametersArray[1]);
        }
    }
}
