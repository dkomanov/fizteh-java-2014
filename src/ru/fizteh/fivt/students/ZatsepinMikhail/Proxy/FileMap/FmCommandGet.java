package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;

public class FmCommandGet extends CommandFileMap {
    public FmCommandGet() {
        name = "get";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        try{
            Storeable value = myFileMap.get(args[1]);
            if (value != null) {
                System.out.println("found\n" + Serializator.serialize(myFileMap, myFileMap.get(args[1])));
            } else {
                System.out.println("not found");
            }
            return true;
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
