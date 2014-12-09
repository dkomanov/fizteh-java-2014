package ru.fizteh.fivt.students.dsalnikov.telnet;


import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProviderFactory;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;

public class RMIServerImpl implements RMIServer, Serializable {

    private final StorableTableProviderFactory factory = new StorableTableProviderFactory();
    private final String path = System.getProperty("fizteh.db.dir");
    protected Set<ClientState> connectedClients = new HashSet<>();
    private volatile boolean isStopped;

    public static void main(String[] args) {
        System.out.println("Server started on 8080");
        RMIServer Server = new RMIServerImpl();
        try {
            Registry registry = LocateRegistry.createRegistry(8080);
            registry.bind("Server", Server);

            while (true) {
                //do nothing
            }


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectUser(ClientState state) {
        connectedClients.add(state);

    }

    @Override
    public RemoteTableProvider getTableProvider() {
        RemoteTableProvider providerToReturn = null;
        try {
            providerToReturn = (RemoteTableProvider) factory.create(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return providerToReturn;
    }

    @Override
    public void disconnectUser(ClientState state) {
        //shutdown working thread if active
        if (!connectedClients.contains(state)) {
            throw new IllegalStateException("user isn't not connected");
        } else {
            //force shutdown
            state.disconnect();
        }
    }

    protected synchronized void stop() {
        this.isStopped = true;
        //shutdown all the clients
        connectedClients.forEach((v) -> v.disconnect());
        connectedClients.clear();
        isStopped = true;
    }

    protected synchronized boolean isStopped() {
        return isStopped;
    }
}
