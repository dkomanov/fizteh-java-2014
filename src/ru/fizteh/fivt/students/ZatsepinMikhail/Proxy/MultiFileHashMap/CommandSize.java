package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

public class CommandSize extends CommandMultiFileHashMap {
    public CommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap myMap, String[] args) {
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(currentTable.size());
        }
        return true;
    }
}
