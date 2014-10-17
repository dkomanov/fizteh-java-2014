package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by mikhail on 16.10.14.
 */
public class CommandCreate extends CommandMultiFileHashMap {
    public CommandCreate() {
        name = "create";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMultiDataBase, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        Path pathOfNewTable = Paths.get(myMultiDataBase.getDataBaseDirectory()
            + System.getProperty("file.separator") + args[1]);
        if (Files.exists(pathOfNewTable) & Files.isDirectory(pathOfNewTable)) {
            System.out.println(args[1] + " exists");
            return true;
        }
        try{
            Files.createDirectory(pathOfNewTable);
            System.out.println("created");
        } catch (IOException e) {
            System.out.println(name + ": error while creating directory");
            return false;
        }
        return true;
    }
}
