package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import java.io.*;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

public class FileMap extends Shell {
    private FileMapState state;
    public final FileMapState getFileMapState() {
        return state;
    }
    public FileMap(final File currentFile) throws IOException {
        state = new FileMapState(currentFile);
        Utils.readDataBase(state);
    }
}
