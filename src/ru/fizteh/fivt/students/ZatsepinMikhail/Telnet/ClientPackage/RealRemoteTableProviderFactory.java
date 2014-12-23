package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;


public class RealRemoteTableProviderFactory implements RemoteTableProviderFactory {
    private RealRemoteTableProvider currentProvider;

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        currentProvider = new RealRemoteTableProvider(hostname, port);
        return currentProvider;
    }

    public void disconnectCurrentProvider() {
        try {
            currentProvider.close();
        } catch (IOException e) {
            System.err.println("error while closing table provider");
        }
        currentProvider = null;
    }

    public RealRemoteTableProvider getCurrentProvider() {
        return currentProvider;
    }
}
