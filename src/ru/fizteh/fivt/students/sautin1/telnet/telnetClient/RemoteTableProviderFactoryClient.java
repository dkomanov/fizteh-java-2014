package ru.fizteh.fivt.students.sautin1.telnet.telnetClient;

import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by sautin1 on 12/15/14.
 */
public class RemoteTableProviderFactoryClient implements RemoteTableProviderFactory {

    public RemoteTableProviderClient connect(String hostname, int port) throws IOException {
        try {
            InetAddress ipAddress = InetAddress.getByName(hostname);
            Socket socket = new Socket(ipAddress, port);
            RemoteTableProviderClient provider = new RemoteTableProviderClient(socket);
            return provider;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

}
