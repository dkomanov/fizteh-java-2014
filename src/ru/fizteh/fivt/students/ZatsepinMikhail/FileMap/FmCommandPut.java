package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage.Serializator;

import java.text.ParseException;

public class FmCommandPut extends CommandFileMap {
    public FmCommandPut() {
        name = "put";
        numberOfArguments = -1;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        Storeable oldValue = myFileMap.get(args[1]);
        try {
            myFileMap.put(args[1], Serializator.deserialize(myFileMap, args[2]));
        } catch (ParseException e) {
            System.err.println("error while deserializing: " + args[2]
                + "; " + e.getMessage());
            return false;
        }
        if (oldValue != null) {
            System.out.println("overwrite\n" + Serializator.serialize(myFileMap, oldValue));
        } else {
            System.out.println("new");
        }
        return true;
    }
}
