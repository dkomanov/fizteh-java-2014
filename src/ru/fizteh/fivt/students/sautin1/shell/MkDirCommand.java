package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * "mkdir" command.
 * Created by sautin1 on 9/30/14.
 */
public class MkDirCommand extends AbstractShellCommand {

    public MkDirCommand() {
        super("mkdir", 1, 1);
    }

    /**
     * Creates new directory in present working directory.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        String dirName;
        dirName = args[1];

        Path dirAbsolutePath = state.getPWD().resolve(dirName).normalize();
        try {
            Files.createDirectory(dirAbsolutePath);
        } catch (IOException e) {
            String exceptionMessage = toString() + ": cannot create directory \'" + dirName + "\': ";
            if (e instanceof FileAlreadyExistsException) {
                exceptionMessage += "File exists";
            } else {
                exceptionMessage += e.getMessage();
            }
            throw new CommandExecuteException(exceptionMessage);
        }
    }

}
