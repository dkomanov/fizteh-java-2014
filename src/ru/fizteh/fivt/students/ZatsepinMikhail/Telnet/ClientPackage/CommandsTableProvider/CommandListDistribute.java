package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.PrintStream;
import java.util.List;

public class CommandListDistribute extends CommandTableProviderExtended {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
            return true;
        }
        List<String> listOfKeys = currentTable.list();
        int counter = 0;
        for (String oneString : listOfKeys) {
            System.out.print(oneString);
            if (++counter < listOfKeys.size()) {
                System.out.print(", ");
            }
        }
        if (counter > 0) {
            System.out.println();
        }
        return true;
    }
}
