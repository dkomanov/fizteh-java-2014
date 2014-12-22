package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.shell.RmCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DropCommand extends MultiFileHashMapCommand {
    public DropCommand() {
        initialize("drop", 2);
    }

    @Override
    public boolean run(MFileHashMap myDataBase, String[] args) {
        RmCommand myRemover = new RmCommand();
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

