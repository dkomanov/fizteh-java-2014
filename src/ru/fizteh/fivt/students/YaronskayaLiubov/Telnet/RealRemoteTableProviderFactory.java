package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 19.12.14.
 */
public class RealRemoteTableProviderFactory implements RemoteTableProviderFactory {
    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return null;
    }
}
