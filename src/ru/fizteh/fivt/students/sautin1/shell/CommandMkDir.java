package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandMkDir extends Command {

    public CommandMkDir() {
        minArgNumber = 1;
        commandName = "mkdir";
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments(args)) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        String dirName;
        dirName = args[1];

        Path dirAbsolutePath = presentWorkingDirectory.resolve(dirName).normalize();
        if (!Files.exists(dirAbsolutePath)) {
            try {
                Files.createDirectory(dirAbsolutePath);
            } catch (IOException e) {
                throw new IOException(toString() + ": cannot create directory \'" + dirName + "\': " + e.getMessage());
            }
        } else {
            throw new IOException(toString() + ": cannot create directory \'" + dirName + "\': File exists");
        }
    }

}
