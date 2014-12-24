package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class RmCommand extends AbstractCommand<FileSystemShellState> {
    public RmCommand() {
        super("rm", "rm [-r] <file|dir>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        if (parameters.size() > 2) {
            throw new IllegalArgumentException("too many arguments!");
        }

        if (parameters.size() == 2) {
            if (!parameters.get(0).equals("-r")) {
                throw new IllegalArgumentException("invalid arguments");
            }

            shellState.getFileSystem().remove(parameters.get(1), true);
        } else {
            shellState.getFileSystem().remove(parameters.get(0), false);
        }
    }
}
