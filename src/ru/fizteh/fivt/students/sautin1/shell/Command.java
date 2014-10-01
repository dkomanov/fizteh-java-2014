package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by sautin1 on 10/1/14.
 */
public abstract class Command {
    protected static Path presentWorkingDirectory = Paths.get("").toAbsolutePath().normalize();
    protected String[] possibleArguments;

    public abstract void execute(String... args) throws RuntimeException, IOException;
    public abstract boolean checkArguments();
    public abstract String toString();
}
