package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;
import ru.fizteh.fivt.students.titov.JUnit.file_map.RemoveFmCommand;

public class RemoveDistributeCommand extends MultiFileHashMapCommand {
    public RemoveDistributeCommand() {
        initialize("remove", 2);
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (isTable(currentTable)) {
            return true;
        }
        RemoveFmCommand removeCommand = new RemoveFmCommand();
        return removeCommand.run(currentTable, args);
    }
}
