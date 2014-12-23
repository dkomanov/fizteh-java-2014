package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;

import java.io.PrintStream;

public class CommandUse extends CommandTableProviderExtended {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        try {
            ((RealRemoteTableProvider) dataBase).setCurrentTable(args[1]);
            System.out.println("using " + args[1]);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
