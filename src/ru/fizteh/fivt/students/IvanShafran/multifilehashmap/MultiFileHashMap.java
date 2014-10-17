package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class MultiFileHashMap extends AbstractShell {

    private void checkWorkingDirectory() throws Exception {
        File workingDirectory;
        try {
            workingDirectory =
                    Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir")).toFile();
        } catch (Exception e) {
            throw new Exception("wrong path to working directory");
        }

        if (!workingDirectory.exists()) {
            throw new Exception("wrong path to working directory");
        } else {
            if (!workingDirectory.isDirectory()) {
                throw new Exception(workingDirectory.toString() + "is not directory");
            }
        }
    }


    MultiFileHashMap() {
        try {
            checkWorkingDirectory();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }
}
