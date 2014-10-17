package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable.CommandUse;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell.*;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.shell.CommandExit;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class MultiFileHashMap extends AbstractShell {
    File workingDirectory;
    DBTable workingDBTable;

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public DBTable getWorkingDBTable() {
        return workingDBTable;
    }

    public void setWorkingDBTable(DBTable dbTable) {
        workingDBTable = dbTable;
    }


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
            } catch (Exception e) {
                throw new Exception("wrong table format: "
                        + Paths.get(workingDirectory.getAbsolutePath(), table).toString());
            }

            if (!tableFile.isDirectory()) {
                throw new Exception(tableFile.toString() + " is not a directory");
            }

            try {
                DBTable dbTable = new DBTable(tableFile);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private void initCommands() {
        this.command = new HashMap<>();

        this.command.put("exit", new CommandExit());
        this.command.put("use", new CommandUse(this));
    }

    MultiFileHashMap() {
        try {
            checkWorkingDirectory();
            checkDBTables();
            initCommands();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }
}
