package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class MvCommand extends AbstractCommand<FileSystemShellState> {
    public MvCommand() {
        super("mv", "mv <source> <destination>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 2) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 2) {
            throw new IllegalArgumentException("too few arguments");
        }

        shellState.getFileSystem().moveFiles(parameters.get(0), parameters.get(1));
    }
}
