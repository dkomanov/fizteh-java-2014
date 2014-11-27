package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

public class CommandRemoveDistribute extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.CommandMultiFileHashMap {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandRemove removeCommand = new ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandRemove();
        return removeCommand.run(currentTable, args);
    }
}
