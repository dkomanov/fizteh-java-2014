package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Client;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.RemoteTableProviderFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseTableOperations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class TelnetClient extends DatabaseTableOperations {
    public static final int DEFAULT_PORT = 10001;
    public ClientState state = ClientState.NOT_CONNECTED;
    String host;
    int port;
    public Socket socket;
    public BufferedReader inputStream;
    public PrintWriter outputStream;

    TelnetClient() {
        super(null);
    }

    public void connect(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        RemoteTableProviderFactory factory = new TelnetRemoteTableProviderFactory();
        provider = factory.connect(host, port);
        inputStream  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new PrintWriter(socket.getOutputStream(), true);
        state = (port == DEFAULT_PORT) ? ClientState.LOCAL : ClientState.REMOTE_HOST_PORT;
    }

    public int disconnect() throws IOException {
        if (state == ClientState.NOT_CONNECTED) {
            return -1;
        } else {
            outputStream.println("exit");
            inputStream.close();
            outputStream.close();
            socket.close();
            ((RemoteTableProvider) provider).close();
            state = ClientState.NOT_CONNECTED;
            return 0;
        }
    }

    public boolean works() {
        return state != ClientState.NOT_CONNECTED;
    }

    @Override
    public int exit() {
        if (provider != null) {
            ((TelnetRemoteTableProvider) provider).close();
        }
        return 0;
    }

    @Override
    public List<String> showTables() {
        return ((TelnetRemoteTableProvider) provider).showTables();
    }

    @Override
    public Table useTable(String name) {
        Table tempTable = provider.getTable(name);
        if (tempTable != null) {
            table = tempTable;
        }
        return tempTable;
    }
}
