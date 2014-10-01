package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 9/30/14.
 */
public class CommandMkDir extends Command {

    public CommandMkDir() {
        minArgNumber = 1;
    }

    @Override
    public void execute(String... args) throws RuntimeException, IOException {
        if (!enoughArguments()) {
            throw new IllegalArgumentException(toString() + ": missing operand");
        }
        String dirName;
        dirName = args[1];

        Path dirAbsolutePath = Paths.get(presentWorkingDirectory.toString(), dirName).toAbsolutePath().normalize();
        /**/System.out.println(dirAbsolutePath.toString());
        try {
            Files.createDirectory(dirAbsolutePath);
        } catch (IOException e) {
            throw new IOException(toString() + ": cannot create directory \'" + dirName + "\': " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "mkdir";
    }
}
