package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;

public class RollbackCommand extends MultiFileHashMapCommand {
    public RollbackCommand() {
        initialize("rollback", 1);
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (!isTable(currentTable)) {
            System.err.println(currentTable.rollback());
        }
        return true;
    }
}
