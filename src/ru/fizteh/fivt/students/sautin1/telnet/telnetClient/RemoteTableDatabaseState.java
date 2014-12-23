package ru.fizteh.fivt.students.sautin1.telnet.telnetClient;

import ru.fizteh.fivt.students.sautin1.telnet.ShellState;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by sautin1 on 12/15/14.
 */
public class RemoteTableDatabaseState extends ShellState {
    protected final RemoteTableProviderFactoryClient factory;
    protected RemoteTableProviderClient provider;

    public RemoteTableDatabaseState(RemoteTableProviderFactoryClient factory, BufferedReader inStream,
                                    PrintWriter outStream) {
        super(inStream, outStream);
        this.factory = factory;
    }

    public RemoteTableProviderFactoryClient getFactory() {
        return factory;
    }

    public void setProvider(RemoteTableProviderClient provider) {
        this.provider = provider;
    }

    public RemoteTableProviderClient getProvider() {
        return provider;
    }

}
