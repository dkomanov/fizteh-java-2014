package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * "cd" command.
 * Created by sautin1 on 9/30/14.
 */
public class CommandCd extends Command {

    public CommandCd() {
        minArgNumber = 0;
        commandName = "cd";
    }

    /**
     * Changes present working directory to args[1]. If (args.length == 1) returns to the home directory.
     * @param args [0] - command name; [1] - (optional) new absolute or relative path
     * @throws IOException
     */
    @Override
    public void execute(String... args) throws IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        if (args.length == 1) {
            presentWorkingDirectory = Paths.get("").toAbsolutePath().normalize();
        } else {
            String dirName;
            dirName = args[1];

            Path dirAbsolutePath = presentWorkingDirectory.resolve(dirName).normalize();
            if (Files.exists(dirAbsolutePath)) {
                if (Files.isDirectory(dirAbsolutePath)) {
                    presentWorkingDirectory = dirAbsolutePath;
                } else {
                    throw new NoSuchFileException(toString() + ": " + dirName + ": Not a directory");
                }
            } else {
                throw new NoSuchFileException(toString() + ": \'" + dirName + "\': No such file or directory");
            }
        }
    }

}
