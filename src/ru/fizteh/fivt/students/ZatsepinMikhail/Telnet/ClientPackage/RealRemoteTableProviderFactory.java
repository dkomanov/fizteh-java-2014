package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;


public class RealRemoteTableProviderFactory implements RemoteTableProviderFactory {
    private RemoteTableProvider currentProvider;

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        currentProvider = new RealRemoteTableProvider(hostname, port);
        return currentProvider;
    }

    public void disconnectCurrentProvider() {
        currentProvider = null;
    }

    public RemoteTableProvider getCurrentProvider() {
        return currentProvider;
    }
}
