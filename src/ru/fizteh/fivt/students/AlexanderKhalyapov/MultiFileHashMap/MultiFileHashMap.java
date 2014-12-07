package ru.fizteh.fivt.students.AlexanderKhalyapov.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.File;
import java.io.IOException;

public class MultiFileHashMap extends Shell {
    private MultiFileHashMapState state;
    public MultiFileHashMap(final File currentFile) throws IOException {
        state = new MultiFileHashMapState(currentFile);
    }
    public final MultiFileHashMapState getMFHMState() {
        return state;
    }
}
