package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.shell.CommandRm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandDrop extends CommandMultiFileHashMap {
    public CommandDrop() {
        name = "drop";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myDataBase, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        CommandRm myRemover = new CommandRm();
        Path pathForRemoveTable = Paths.get(myDataBase.getDataBaseDirectory(), args[1]);
        if (!Files.exists(pathForRemoveTable)) {
            System.out.println(args[1] + " not exists");
            return true;
        }
        String[] argsArray = {
                "rm",
                "-r",
                pathForRemoveTable.toString()
        };
        if (myRemover.run(argsArray)) {
            System.out.println("dropped");
            myDataBase.dropTable(args[1]);
            return true;
        } else {
            System.err.println(name + " : error while removing table's directory");
            return false;
        }
    }
}
