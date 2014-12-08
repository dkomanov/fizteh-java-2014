package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;

import java.io.IOException;

public class CommandCommit extends CommandMultiFileHashMap{
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            try {
                System.out.println(currentTable.commit());
            } catch (IOException e) {
                System.err.println("io exception while writing in file");
                return false;
            }
        }
        return true;
    }
}
