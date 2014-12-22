package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;


public class ShowTablesCommand extends MultiFileHashMapCommand {
    public ShowTablesCommand() {
        initialize("show", 2);
    }

    public void showTables() {
        Map<String, FileMap> tables = MFileHashMap.getDataBaseTables();
        Set<Entry<String, FileMap>> pairSet = tables.entrySet();
        for (Entry<String, FileMap> oneTable: pairSet) {
            System.err.println(oneTable.getKey() + " "
                    + oneTable.getValue().size());
        }
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (!args[1].equals("tables")) {
            System.out.println(name + ": wrong arguments");
            return false;
        }
        showTables();
        return true;
    }
}
