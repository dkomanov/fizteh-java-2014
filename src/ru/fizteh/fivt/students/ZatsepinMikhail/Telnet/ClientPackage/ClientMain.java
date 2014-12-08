package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections.ClientCmdConnect;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections.ClientCmdDisconnect;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections.ClientCmdWhereAmI;

public class ClientMain {
    public static void main(String[] args) {
        RealRemoteTableProviderFactory factory = new RealRemoteTableProviderFactory();
        ShellExtended myShell = new ShellExtended(factory);
        setUpShell(myShell);
        myShell.interactiveMode();
    }

    public static void setUpShell(ShellExtended myShell) {
        myShell.addCommand(new ClientCmdConnect());
        myShell.addCommand(new ClientCmdDisconnect());
        myShell.addCommand(new ClientCmdWhereAmI());
    }
}
