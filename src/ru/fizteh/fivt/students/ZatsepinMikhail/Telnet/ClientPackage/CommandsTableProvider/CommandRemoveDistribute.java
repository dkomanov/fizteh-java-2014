package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.PrintStream;

public class CommandRemoveDistribute extends CommandTableProviderExtended {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return true;
        }
        System.out.println(currentTable.remove(args[1]));
        return true;
    }
}
