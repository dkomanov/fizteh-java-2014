package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Shell;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.*;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClient.ClientCmdConnect;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClient.ClientCmdDisconnect;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClient.ClientCmdWhereAmI;

public class ClientMain {
    public static void main(String[] args) {
        RealRemoteTableProviderFactory factory = new RealRemoteTableProviderFactory();
        Shell<RemoteTableProviderFactory> myShell = new Shell<>(factory);
        setUpShell(myShell);
        myShell.interactiveMode();
    }

    public static void setUpShell(Shell<RemoteTableProviderFactory> myShell) {
        myShell.addCommand(new ClientCmdConnect());
        myShell.addCommand(new ClientCmdDisconnect());
        myShell.addCommand(new ClientCmdWhereAmI());
    }
}
