package ru.fizteh.fivt.students.gudkov394.Telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

/**
 * Created by kagudkov on 07.12.14.
 */
public class RemoteTableProviderFactoryClass implements RemoteTableProviderFactory {

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return null;
    }
}
