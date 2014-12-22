package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;
import ru.fizteh.fivt.students.titov.JUnit.file_map.PutFileMapCommand;

public class PutDistributeCommand extends MultiFileHashMapCommand {
    public PutDistributeCommand() {
        initialize("put", 3);
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (isTable(currentTable)) {
            return true;
        }
        PutFileMapCommand commandPut = new PutFileMapCommand();
        return commandPut.run(currentTable, args);
    }
}
