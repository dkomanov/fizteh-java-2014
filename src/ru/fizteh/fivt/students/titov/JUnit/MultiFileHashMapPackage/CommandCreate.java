package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class CommandCreate extends CommandMultiFileHashMap {
    public CommandCreate() {
        name = "create";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMultiDataBase, String[] args) {
        Path pathOfNewTable = Paths.get(myMultiDataBase.getDataBaseDirectory()
                + File.separator + args[1]);
        if (Files.exists(pathOfNewTable) & Files.isDirectory(pathOfNewTable)) {
            System.err.println(args[1] + " exists");
            return true;
        }
        try {
            Files.createDirectory(pathOfNewTable);
            System.err.println("created");
            myMultiDataBase.addTable(args[1], new FileMap(pathOfNewTable.toString()));
        } catch (IOException e) {
            System.err.println(name + ": error while creating directory");
            return false;
        }
        return true;
    }
}

