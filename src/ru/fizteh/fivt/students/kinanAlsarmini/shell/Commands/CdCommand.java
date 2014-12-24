package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class CdCommand extends AbstractCommand<FileSystemShellState> {
    public CdCommand() {
        super("cd", "cd <absolute path|relative path>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() > 0) {
            shellState.getFileSystem().setWorkingDirectory(parameters.get(0));
        }
    }
}
