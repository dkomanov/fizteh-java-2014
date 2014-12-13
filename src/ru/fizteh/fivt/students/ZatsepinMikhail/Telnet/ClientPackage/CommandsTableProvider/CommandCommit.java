package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class CommandCommit extends CommandTableProviderExtended {
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        RealRemoteTable currentTable = ((RealRemoteTableProvider) dataBase).getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            try {
                System.out.println(currentTable.commit());
            } catch (IOException e) {
                System.err.println("io exception while writing in file");
                return false;
            }
        }
        return true;
    }
}
