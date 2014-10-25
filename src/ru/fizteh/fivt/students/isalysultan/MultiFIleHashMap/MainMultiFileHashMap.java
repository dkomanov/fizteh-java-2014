package ru.fizteh.fivt.students.isalysultan.MultiFileHashMap;

import java.io.IOException;

public class MainMultiFileHashMap {

    public static void main(String[] argv) throws IOException {
        RootDirectory newDirect = new RootDirectory();
        if (argv.length == 0) {
            InteractiveMode.Interactive(newDirect);
        } else {
            BatchMode.batchParser(newDirect, argv);
        }
    }
}
