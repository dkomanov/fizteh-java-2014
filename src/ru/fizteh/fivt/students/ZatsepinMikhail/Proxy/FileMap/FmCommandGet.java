package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;

public class FmCommandGet extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.CommandFileMap {
    public FmCommandGet() {
        name = "get";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap myFileMap, String[] args) {
        Storeable value = myFileMap.get(args[1]);
        if (value != null) {
            System.out.println("found\n" + ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator.serialize(myFileMap, myFileMap.get(args[1])));
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
