package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.Command;
import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;

public abstract class MultiFileHashMapCommand extends Command<MFileHashMap> {

    public void initialize(String commandName, int n) {
        name = commandName;
        numberOfArguments = n;
    }

    public boolean isTable(FileMap currentTable) {
        if (currentTable == null) {
            System.out.println("no table");
            return true;
        }
        return false;
    }
}
