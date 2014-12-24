package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class MakeDirCommand extends AbstractCommand<FileSystemShellState> {
    public MakeDirCommand() {
        super("mkdir", "mkdir <dirname>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        shellState.getFileSystem().createDirectory(parameters.get(0));
    }
}
