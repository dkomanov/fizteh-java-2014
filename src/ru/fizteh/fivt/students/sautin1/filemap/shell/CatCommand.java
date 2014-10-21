package ru.fizteh.fivt.students.sautin1.filemap.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * "cat" command.
 * Created by sautin1 on 9/30/14.
 */
public class CatCommand extends AbstractShellCommand {

    public CatCommand() {
        super("cat", 1, 1);
    }

    /**
     * Prints contents of the file from args[1].
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }

        String fileName = args[1];
        Path fileAbsolutePath = state.getPWD().resolve(fileName).normalize();

        if (Files.exists(fileAbsolutePath)) {
            if (!Files.isDirectory(fileAbsolutePath)) {
                try (Scanner scanner = new Scanner(Files.newInputStream(fileAbsolutePath))) {
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());
                    }
                } catch (IOException e) {
                    throw new CommandExecuteException(toString() + " : " + e.getMessage());
                }
            } else {
                throw new CommandExecuteException(toString() + ": " + fileName + ": Is a directory");
            }
        } else {
            throw new CommandExecuteException(toString() + ": " + fileName + ": No such file or directory");
        }
    }

}
