package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;

public class DirCommand extends AbstractCommand<FileSystemShellState> {
    public DirCommand() {
        super("ls", "ls");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        if (params.length() > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        String[] files = shellState.getFileSystem().listWorkingDirectory();

        if (files == null) {
            return;
        }

        for (final String file : files) {
            System.out.println(file);
        }
    }
}
