package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TableProviderFactoryRemoteExtenderImpl implements RemoteTableProviderFactory {

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        RemoteTableProvider providerToReturn = null;
        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            RMIServer server = (RMIServer) registry.lookup("Server");
            providerToReturn = server.getTableProvider();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return providerToReturn;
    }
}
