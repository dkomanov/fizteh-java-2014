package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.IOException;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class CRemoteTableProviderFactory implements RemoteTableProviderFactory {

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return new CRemoteTableProvider(hostname, port);
    }
}
