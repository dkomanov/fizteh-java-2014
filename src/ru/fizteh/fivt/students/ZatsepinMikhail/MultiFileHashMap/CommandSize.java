package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

public class CommandSize extends CommandMultiFileHashMap {
    public CommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }

        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(currentTable.size());
        }
        return true;
    }
}
