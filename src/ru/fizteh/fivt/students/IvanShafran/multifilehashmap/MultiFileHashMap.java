package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MultiFileHashMap extends AbstractShell {
    File workingDirectory;
    DBTable workingDBTable;

    private void checkWorkingDirectory() throws Exception {
        File workingDirectoryTest;
        try {
            workingDirectoryTest =
                    Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir")).toFile();
        } catch (Exception e) {
            throw new Exception("wrong path to working directory");
        }

        if (!workingDirectoryTest.exists()) {
            throw new Exception("wrong path to working directory");
        }

        if (!workingDirectoryTest.isDirectory()) {
            throw new Exception(workingDirectoryTest.toString() + "is not a directory");
        }

        workingDirectory = workingDirectoryTest;
    }

    private void checkDBTables() throws Exception {
        for (String table : workingDirectory.list()) {
            File tableFile;
            try {
                tableFile = new File(Paths.get(workingDirectory.getAbsolutePath(), table).toString());
            }
            catch (Exception e) {
                throw new Exception("wrong table format: "
                        + Paths.get(workingDirectory.getAbsolutePath(), table).toString());
            }

            if (!tableFile.isDirectory()) {
                throw new Exception(tableFile.toString() + " is not a directory");
            }

            try {
                DBTable dbTable = new DBTable(tableFile);
            }
            catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    MultiFileHashMap() {
        try {
            checkWorkingDirectory();
            checkDBTables();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }
}
