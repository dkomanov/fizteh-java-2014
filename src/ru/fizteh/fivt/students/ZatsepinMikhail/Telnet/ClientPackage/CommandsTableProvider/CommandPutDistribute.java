package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.StoreablePackage.Serializator;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.NoSuchElementException;

public class CommandPutDistribute extends CommandTableProviderExtended {
    public CommandPutDistribute() {
        name = "put";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return true;
        }
        Storeable oldValue = currentTable.get(args[1]);
        try {
            currentTable.put(args[1], Serializator.deserialize(currentTable, args[2]));
        } catch (ParseException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
            return false;
        } catch (NoSuchElementException e) {
            System.out.println("error: not xml format value");
            return false;
        }
        if (oldValue != null) {
            System.out.println("overwrite\n" + Serializator.serialize(currentTable, oldValue));
        } else {
            System.out.println("new");
        }
        return true;
    }
}
