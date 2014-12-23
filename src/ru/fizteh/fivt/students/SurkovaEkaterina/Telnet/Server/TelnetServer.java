package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseTableOperations;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class TelnetServer extends DatabaseTableOperations {
    List<ConnectionsProvider> users;

    public TelnetServer(TableProvider provider) {
        super(provider);
        users = new ArrayList<>();
    }

    public void start(int port) throws Exception {
        ServerSocket socket = new ServerSocket(port);
        ConnectionsProvider user = new ConnectionsProvider(socket, this);
        user.start();
        users.add(user);
    }

    public int stop() throws Exception {
        ServerSocket targetSocket = users.get(users.size() - 1).socket;
        int port = targetSocket.getLocalPort();
        targetSocket.close();
        users.remove(users.size() - 1);
        return port;
    }

    public List<String> listUsers() {
        List<String> result = new ArrayList<>();
        for (ConnectionsProvider user: users) {
            for (ConnectedClient connection: user.clients) {
                if (connection.serverWorks) {
                    String userName = connection.socket.getInetAddress().getHostName();
                    int port = user.socket.getLocalPort();
                    result.add(userName + ":" + port);
                }
            }
        }
        return result;
    }

    public boolean works() {
        return users.size() != 0;
    }
}

