package ru.fizteh.fivt.students.VasilevKirill.telnet.structures;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Kirill on 07.12.2014.
 */
public class MyRemoteTableProviderFactory implements RemoteTableProviderFactory {
    private Socket socket;

    public MyRemoteTableProviderFactory(Socket socket) {
        this.socket = socket;
    }

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        RemoteTableProvider tableProvider = new MyRemoteTableProvider(socket);
        return tableProvider;
    }
}
