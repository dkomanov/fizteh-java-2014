package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;

public class FmCommandRemove extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.CommandFileMap {
    public FmCommandRemove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap myFileMap, String[] args) {
        Storeable value = myFileMap.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
