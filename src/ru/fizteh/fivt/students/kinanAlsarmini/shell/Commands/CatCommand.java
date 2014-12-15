package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.FileSystemShellState;

import java.io.IOException;
import java.util.ArrayList;

public class CatCommand extends AbstractCommand<FileSystemShellState> {
    public CatCommand() {
        super("cat", "cat <file>");
    }

    public void executeCommand(String params, FileSystemShellState shellState) throws IOException {
        ArrayList<String> parameters = CommandParser.parseParams(params);

        if (parameters.size() < 1) {
            throw new IllegalArgumentException("too few arguments");
        }

        if (parameters.size() > 1) {
            throw new IllegalArgumentException("too many arguments");
        }

        ArrayList<String> lines = shellState.getFileSystem().getFileLines(parameters.get(0));

        if (lines == null) {
            return;
        }

        for (final String line : lines) {
            System.out.println(line);
        }
    }
}
