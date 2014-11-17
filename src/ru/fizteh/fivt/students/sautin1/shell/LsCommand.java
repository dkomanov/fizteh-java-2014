package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * "ls" command.
 * Created by sautin1 on 9/30/14.
 */
public class LsCommand extends AbstractShellCommand {

    public LsCommand() {
        super("ls", 0, 0);
    }


    /**
     * Lists present working directory contents.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(state.getPWD())) {
            for (Path entry : directoryStream) {
                Path filePath = entry.getFileName();
                if (!Files.isHidden(filePath)) {
                    System.out.println(filePath.toString());
                }
            }
        } catch (IOException e) {
            throw new CommandExecuteException(toString() + ": " + e.getMessage());
        }
    }

}
