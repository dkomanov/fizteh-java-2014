package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class PwdCommand extends AbstractCommand<FileSystemShellState> {
    public PwdCommand() {
        super("pwd", "pwd");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        String path = shellState.getFileSystem().getWorkingDirectory();

        System.out.println(path);
    }
}
