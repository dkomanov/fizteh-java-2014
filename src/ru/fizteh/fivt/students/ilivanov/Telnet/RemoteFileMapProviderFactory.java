package ru.fizteh.fivt.students.ilivanov.Telnet;

import ru.fizteh.fivt.students.ilivanov.Telnet.Interfaces.RemoteTableProviderFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class RemoteFileMapProviderFactory implements RemoteTableProviderFactory, AutoCloseable {
    private ArrayList<RemoteFileMapProvider> providers;
    private volatile boolean valid;

    public RemoteFileMapProviderFactory() {
        providers = new ArrayList<>();
        valid = true;
    }

    private void checkState() {
        if (!valid) {
            throw new IllegalStateException("factory is closed");
        }
    }

    public RemoteFileMapProvider connect(String hostname, int port) throws IOException {
        checkState();
        if (hostname == null) {
            throw new IllegalArgumentException("Null hostname as argument");
        }
        Socket socket = new Socket(hostname, port);   //UnknownHostException, IllegalArgumentException, IOException
        if (!socket.isConnected()) {
            new RuntimeException("Failed to connect");
        }
        RemoteFileMapProvider newProvider = new RemoteFileMapProvider(socket, hostname, port);
        providers.add(newProvider);
        return newProvider;
    }

    public void close() throws IOException {
        if (!valid) {
            return;
        }
        for (RemoteFileMapProvider provider : providers) {
            provider.close();
        }
        valid = false;
    }
}
