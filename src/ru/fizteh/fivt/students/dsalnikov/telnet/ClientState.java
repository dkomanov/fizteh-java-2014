package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientState implements Serializable {

    private volatile boolean isConnected;
    private RemoteTableProvider currentTableProvider;
    private String currentlyUsedHostName;
    private int currentlyUsedPort;
    private RMIServer link;
    private String clientHost;
    private Thread currentThread;


    public ClientState() {
        try {
            clientHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void connect(String hostname, int port) {
        //establish resourses
        try {
            Registry registry = LocateRegistry.getRegistry(hostname, port);
            link = (RMIServer) registry.lookup("Server");
            currentTableProvider = new TableProviderFactoryRemoteExtenderImpl().connect(hostname, port);
        } catch (IOException e) {
            throw new IllegalStateException("In method connect. connection failed.");
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        currentlyUsedHostName = hostname;
        currentlyUsedPort = port;
        isConnected = true;
        Thread t = new Thread(new NewWorkerRunnable(currentTableProvider));
        currentThread = t;
        //notify remote server of connection
        link.connectUser(this);
        System.err.println("Getting remote shell...Connected");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        link.disconnectUser(this);
    }

    public String whereAmI() {
        return clientHost;
    }
}
