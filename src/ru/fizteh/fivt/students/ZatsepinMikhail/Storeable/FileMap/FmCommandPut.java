package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.Serializator;

import java.text.ParseException;
import java.util.NoSuchElementException;

public class FmCommandPut extends CommandFileMap {
    public FmCommandPut() {
        name = "put";
        numberOfArguments = -1;
    }
    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length < 3) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        Storeable oldValue = myFileMap.get(args[1]);
        try {
            myFileMap.put(args[1], Serializator.deserialize(myFileMap, args[2]));
        } catch (ParseException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            return false;
        } catch (NoSuchElementException e) {
            System.out.println("error: not xml format value");
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
