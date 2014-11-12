package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import java.io.IOException;

public class CommandDrop extends CommandMultiFileHashMap {
    public CommandDrop() {
        name = "drop";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myDataBase, String[] args) {
        try {
            myDataBase.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IOException e) {
            System.err.println("io exception while removing directory");
            return false;
        }
        return true;
    }
}
