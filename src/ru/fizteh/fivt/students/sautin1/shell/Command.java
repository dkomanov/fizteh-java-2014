package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 10/1/14.
 */
public abstract class Command {
    protected static Path presentWorkingDirectory = Paths.get("").toAbsolutePath().normalize();
    protected int minArgNumber;

    public abstract void execute(String... args) throws RuntimeException, IOException;
    public abstract String toString();

    public boolean enoughArguments(String... args)
    {
        return (args.length >= minArgNumber + 1);
    }
}
