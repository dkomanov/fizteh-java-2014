package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Cd implements Command {
    public final String getName() {
        return "cd";
    }
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (1 == args.length) {
            Path thePath = shell.getState().getPath().resolve(args[0]);
            File theFile = new File(thePath.toString());
            if (!theFile.isDirectory()) {
                throw new IOException("Directory doesn't exist");
            }
            shell.setState(thePath.normalize());
        } else {
            throw new IOException("Incorrect number of arguments");
        }
    }
}
