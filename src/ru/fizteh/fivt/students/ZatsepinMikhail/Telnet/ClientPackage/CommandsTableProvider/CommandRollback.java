package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.PrintStream;

public class CommandRollback extends CommandTableProviderExtended {
    public CommandRollback() {
        name = "rollback";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(currentTable.rollback());
        }
        return true;
    }
}
