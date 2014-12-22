package ru.fizteh.fivt.students.titov.JUnit.multi_file_hash_map;

import ru.fizteh.fivt.students.titov.JUnit.file_map.FileMap;

public class UseCommand extends MultiFileHashMapCommand {
    private static final String USING = "using ";

    public UseCommand() {
        initialize("use", 2);
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap newCurrentTable = myMap.findTableByName(args[1]);
        if (newCurrentTable != null) {
            FileMap currentTable = myMap.getCurrentTable();
            if (currentTable == null) {
                myMap.setCurrentTable(newCurrentTable);
                System.out.println(USING + args[1]);
            } else if (currentTable.getNumberOfUncommitedChanges() > 0) {
                System.out.println(currentTable.getNumberOfUncommitedChanges() + " unsaved changes");
            } else {
                myMap.setCurrentTable(newCurrentTable);
                System.out.println(USING + args[1]);
            }
        } else {
            System.err.println(args[1] + " not exists");
        }
        return true;
    }
}
