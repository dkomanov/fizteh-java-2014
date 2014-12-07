package ru.fizteh.fivt.students.sautin1.shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * "cd" command.
 * Created by sautin1 on 9/30/14.
 */
public class CdCommand extends AbstractShellCommand {

    public CdCommand() {
        super("cd", 0, 1);
    }

    /**
     * Changes present working directory to args[1]. If (args.length == 1) returns to the home directory.
     * @param state - ShellState.
     * @param args - arguments.
     * @throws CommandExecuteException if any error occurs.
     */
    @Override
    public void execute(ShellState state, String... args) throws CommandExecuteException {
        if (checkArgumentNumber(args) != CheckArgumentNumber.EQUAL) {
            throw new CommandExecuteException(toString() + ": wrong number of arguments");
        }

        if (args.length - 1 == 0) {
            state.setPWD(Paths.get("").toAbsolutePath().normalize());
        } else {
            String dirName;
            dirName = args[1];

            Path dirAbsolutePath = state.getPWD().resolve(dirName).normalize();
            if (Files.exists(dirAbsolutePath)) {
                if (Files.isDirectory(dirAbsolutePath)) {
                    state.setPWD(dirAbsolutePath);
                } else {
                    throw new CommandExecuteException(toString() + ": " + dirName + ": Not a directory");
                }
            } else {
                throw new CommandExecuteException(toString() + ": \'" + dirName + "\': No such file or directory");
            }
        }
    }

}
