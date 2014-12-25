package ru.fizteh.fivt.students.dsalnikov.telnet;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.stream.Collectors;

public class ServerState implements Runnable {

    RMIServerImpl delegate = new RMIServerImpl();


    public void start(int port) {
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("Server", delegate);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        System.err.println(String.format("stared server on port %s", port));
        while (!delegate.isStopped()) {
            //run
        }
    }

    public void stop() {
        delegate.stop();
    }

    public List<String> listUsers() {
        return delegate.connectedClients.stream().map(ClientState::whereAmI).collect(Collectors.toList());
    }


    public void exit() {
        delegate.stop();
        System.exit(0);
    }

    @Override
    public void run() {

    }
}
