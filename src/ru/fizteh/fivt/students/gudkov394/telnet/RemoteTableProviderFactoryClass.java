package ru.fizteh.fivt.students.gudkov394.telnet;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;

import java.io.IOException;

public class RemoteTableProviderFactoryClass implements RemoteTableProviderFactory {

    @Override
    public RemoteTableProvider connect(String hostname, int port) throws IOException {
        return null;
    }
}
