package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;

import java.rmi.Remote;

public interface RMIServer extends Remote {


    void connectUser(ClientState state);

    RemoteTableProvider getTableProvider();

    void disconnectUser(ClientState state);
}
