package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseTableProviderFactory;

import java.io.IOException;

class TelnetRemoteTableProviderFactory
        extends DatabaseTableProviderFactory implements RemoteTableProviderFactory {
    @Override
    public RemoteTableProvider connect(String hostName, int port) throws IOException {
        if (hostName == null) {
            throw new IllegalArgumentException();
        }
        return new TelnetRemoteTableProvider(hostName, port);
    }
}

