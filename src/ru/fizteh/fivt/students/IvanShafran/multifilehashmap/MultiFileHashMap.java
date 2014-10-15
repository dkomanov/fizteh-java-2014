package ru.fizteh.fivt.students.IvanShafran.multifilehashmap;

import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.commands.*;
import ru.fizteh.fivt.students.IvanShafran.multifilehashmap.abstractShell.AbstractShell;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

public class MultiFileHashMap extends AbstractShell {

    private void checkWorkingFile() throws Exception {
        File workingFile;
        try {
            workingFile =
                    Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file")).toFile();
        } catch (Exception e) {
            throw new Exception("wrong path to db file");
        }

        if (!workingFile.exists() || workingFile.isDirectory()) {
            throw new Exception("wrong path to db file");
        }
    }

    private void initCommands() {
        command = new HashMap<>();

        command.put("put", new CommandPut(dataBaseFile));
        command.put("exit", new CommandExit(dataBaseFile));
        command.put("get", new CommandGet(dataBaseFile));
        command.put("list", new CommandList(dataBaseFile));
        command.put("remove", new CommandRemove(dataBaseFile));
    }

    MultiFileHashMap() {
        try {
            checkWorkingFile();
            initCommands();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }
}
