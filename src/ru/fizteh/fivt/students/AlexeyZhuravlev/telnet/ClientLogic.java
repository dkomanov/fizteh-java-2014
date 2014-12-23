package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class ClientLogic {

    RemoteTableProvider remoteProvider;
    ShellTableProvider shellProvider;
    String hostname;
    int portNumber;

    public void connect(String passedHost, int passedPort) throws IOException {
        hostname = passedHost;
        portNumber = passedPort;
        RemoteTableProviderFactory factory = new MyRemoteTableProviderFactory();
        remoteProvider = factory.connect(hostname, portNumber);
        shellProvider = new ShellTableProvider(remoteProvider);
    }

    public boolean isConnected() {
        return remoteProvider != null;
    }

    public void disconnect() throws IOException {
        remoteProvider.close();
        remoteProvider = null;
    }

    public String getPosition() {
        if (!isConnected()) {
            return "local";
        } else {
            return "remote" + " " + hostname + " " + portNumber;
        }
    }

    public ShellTableProvider getShellProvider() {
        return shellProvider;
    }
}
