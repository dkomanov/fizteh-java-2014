package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.Put;

/**
 * Created by mikhail on 19.10.14.
 */
public class CommandPutDistribute extends CommandMultiFileHashMap {
    public CommandPutDistribute() {
        name = "put";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }

        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        Put commandPut = new Put();
        return commandPut.run(currentTable, args);
    }
}
