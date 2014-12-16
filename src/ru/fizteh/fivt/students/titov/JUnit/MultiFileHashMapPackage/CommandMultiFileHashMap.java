package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.Command;
import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;

public abstract class CommandMultiFileHashMap extends Command<MFileHashMap> {

    public boolean isTable(FileMap currentTable) {
        if (currentTable == null) {
            System.err.println("no table");
            return true;
        }
        return false;
    }
}
