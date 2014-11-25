package ru.fizteh.fivt.students.ivan_ivanov.shell;

import java.nio.file.Path;

public class ShellState {

    private Path path;

    final Path getPath() {

        return path;
    }

    final void setPath(final Path inPath) {

        path = inPath;
    }
}
