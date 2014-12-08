package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsClientConnections;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProviderFactory;

public class ClientCmdWhereAmI extends ClientCommand {
    public ClientCmdWhereAmI() {
        name = "whereami";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(RemoteTableProviderFactory factory, String[] args) {
        RealRemoteTableProviderFactory realFactory = (RealRemoteTableProviderFactory) factory;
        RealRemoteTableProvider currentProvider = (RealRemoteTableProvider) realFactory.getCurrentProvider();
        if (currentProvider == null) {
            System.out.println("local");
        } else {
            System.out.println("remote " + currentProvider.getHostName() + " " + currentProvider.getPort());
        }
        return true;
    }
}
