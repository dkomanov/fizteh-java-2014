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
    protected String commandName;

    public abstract void execute(String... args) throws RuntimeException, IOException;

    @Override
    public String toString() {
        return commandName;
    }

    public boolean enoughArguments(String... args) {
        return (args.length >= minArgNumber + 1);
    }
}
