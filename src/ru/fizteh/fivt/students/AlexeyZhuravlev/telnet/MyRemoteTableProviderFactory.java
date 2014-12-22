package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

/**
 * @author AlexeyZhuravlev
 */
public class MyRemoteTableProviderFactory implements RemoteTableProviderFactory {
    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        if (hostname == null) {
            throw new IllegalArgumentException();
        }
        return new MyRemoteTableProvider(hostname, port);
    }
}
