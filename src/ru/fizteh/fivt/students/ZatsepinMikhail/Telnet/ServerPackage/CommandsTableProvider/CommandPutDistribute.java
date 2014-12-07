package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.NoSuchElementException;

public class CommandPutDistribute extends CommandTableProvider {
    public CommandPutDistribute() {
        name = "put";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            output.println("no table");
            return true;
        }
        Storeable oldValue = currentTable.get(args[1]);
        try {
            currentTable.put(args[1], Serializator.deserialize(currentTable, args[2]));
        } catch (ParseException e) {
            output.println("wrong type (" + e.getMessage() + ")");
            return false;
        } catch (NoSuchElementException e) {
            output.println("error: not xml format value");
            return false;
        }
        if (oldValue != null) {
            output.println("overwrite\n" + Serializator.serialize(currentTable, oldValue));
        } else {
            output.println("new");
        }
        return true;
    }
}
