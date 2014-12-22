package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class CreateCommand extends MultiFileHashMapCommand {
    public CreateCommand() {
        initialize("create", 2);
    }

    @Override
    public boolean run(MFileHashMap myMultiDataBase, String[] args) {
        Path pathOfNewTable = Paths.get(myMultiDataBase.getDataBaseDirectory()
                + File.separator + args[1]);
        if (Files.exists(pathOfNewTable) & Files.isDirectory(pathOfNewTable)) {
            System.out.println(args[1] + " exists");
            return true;
        }
        try {
            Files.createDirectory(pathOfNewTable);
            System.out.println("created");
            myMultiDataBase.addTable(args[1], new FileMap(pathOfNewTable.toString()));
        } catch (IOException e) {
            System.err.println(name + ": error while creating directory");
            return false;
        }
        return true;
    }
}

