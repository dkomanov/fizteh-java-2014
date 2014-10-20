package ru.fizteh.fivt.students.IvanShafran.filemap;

import ru.fizteh.fivt.students.IvanShafran.filemap.commands.*;
import ru.fizteh.fivt.students.IvanShafran.filemap.abstractShell.AbstractShell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileMap extends AbstractShell {
    private DBFile dataBaseFile;

    private void checkWorkingFile() throws Exception {
        File workingFile;
        try {
            workingFile =
                    Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file")).toFile();
        } catch (Exception e) {
            throw new Exception("wrong path to db file");
        }

        if (workingFile.isDirectory()) {
            throw new Exception("wrong path to db file");
        }

        if (!workingFile.exists()) {
            try {
                Files.createFile(Paths.get(workingFile.getAbsolutePath()));
            } catch (Exception e) {
                throw new Exception("error: file didn't create");
            }

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

    private void initDBFile() throws Exception {
        File workingFile;
        try {
            workingFile =
                    Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file")).toFile();
        } catch (Exception e) {
            throw new Exception("wrong path to db file");
        }

        dataBaseFile = new DBFile(workingFile);

        try {
            dataBaseFile.readFile();
        } catch (Exception e) {
            printException("file didn't read");
        }
    }

    FileMap() {
        try {
            checkWorkingFile();
            initDBFile();
            initCommands();
        } catch (Exception e) {
            printException(e.getMessage());
        }
    }
}
