package ru.fizteh.fivt.students.AlexanderKhalyapov.Shell;

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
