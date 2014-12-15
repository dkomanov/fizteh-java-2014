package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.util.ArrayList;
import java.io.IOException;

public class CopyCommand extends AbstractCommand<FileSystemShellState> {
    public CopyCommand() {
        super("cp", "cp [-r] <source> <destination>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 3) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 2) {
            throw new IllegalArgumentException("too few arguments");
        }

        if (parameters.size() == 3) {
            if (!parameters.get(0).equals("-r")) {
                throw new IllegalArgumentException("invalid arguments");
            }

            shellState.getFileSystem().copyFiles(parameters.get(1), parameters.get(2), true);
        } else {
            shellState.getFileSystem().copyFiles(parameters.get(0), parameters.get(1), false);
        }
    }
}
