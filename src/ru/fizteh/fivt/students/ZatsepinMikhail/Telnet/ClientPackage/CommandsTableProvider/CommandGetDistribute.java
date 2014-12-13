package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.PrintStream;

public class CommandGetDistribute extends CommandTableProviderExtended {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return true;
        }
        Storeable value = currentTable.get(args[1]);
        if (value != null) {
            System.out.println("found");
            System.out.println(currentTable.get(args[1]));
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
