package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

public class RealRemotetableProviderFactory implements RemoteTableProviderFactory {
    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return null;
    }
}
