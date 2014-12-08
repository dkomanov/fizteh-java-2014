package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandExecutor;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandExtended;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;

import java.io.IOException;

public class ClientCmdConnect extends CommandExtended<RemoteTableProviderFactory> {
    public ClientCmdConnect() {
        name = "connect";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(RemoteTableProviderFactory factory, String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("not connected: wrong argument \"" + args[2] + "\" - it should be a number");
            return false;
        }
        try {
            factory.connect(args[1], port);
            System.out.println("connected");
            return true;
        } catch (IOException e) {
            System.out.println("not connected: " + e.getMessage());
            return false;
        }
    }
}
