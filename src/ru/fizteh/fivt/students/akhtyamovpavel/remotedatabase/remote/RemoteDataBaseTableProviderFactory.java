package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;

import java.io.IOException;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class RemoteDataBaseTableProviderFactory implements RemoteTableProviderFactory {

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return new RemoteDataBaseTableProvider(null, hostname, port);
    }

    public RemoteTableProvider connect(DataBaseTableProvider provider, String hostname, int port) throws IOException {
        return new RemoteDataBaseTableProvider(provider, hostname, port);
    }
}
