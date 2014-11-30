package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile.CommandGet;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile.CommandList;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile.CommandPut;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbfile.CommandRemove;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable.CommandCreate;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable.CommandDrop;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.dbtable.CommandShow;
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
            workingDirectoryTest.mkdirs();
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

        this.command.put("exit", new CommandExit(this));
        this.command.put("use", new CommandUse(this));
        this.command.put("put", new CommandPut(this));
        this.command.put("get", new CommandGet(this));
        this.command.put("list", new CommandList(this));
        this.command.put("remove", new CommandRemove(this));
        this.command.put("create", new CommandCreate(this));
        this.command.put("drop", new CommandDrop(this));
        this.command.put("show", new CommandShow(this));
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
