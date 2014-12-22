package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class ServerLogic {

    TableProvider provider;
    List<ConnectionListener> listeners;

    public ServerLogic(TableProvider passedProvider) {
        provider = passedProvider;
        listeners = new ArrayList<>();
    }

    public void start(int portNumber) throws Exception {
        ServerSocket socket = new ServerSocket(portNumber);
        ConnectionListener listener = new ConnectionListener(socket, provider);
        listener.start();
        listeners.add(listener);
    }

    public int stop() throws Exception {
        ServerSocket targetSocket = listeners.get(listeners.size() - 1).socket;
        int port = targetSocket.getLocalPort();
        targetSocket.close();
        listeners.remove(listeners.size() - 1);
        return port;
    }

    public List<String> getListeners() {
        List<String> result = new ArrayList<>();
        for (ConnectionListener listener: listeners) {
            for (ClientCommunicator connection: listener.connections) {
                if (connection.serverAlive) {
                    String user = connection.socket.getInetAddress().getHostName();
                    int port = listener.socket.getLocalPort();
                    result.add(user + ":" + port);
                }
            }
        }
        return result;
    }

    public boolean isStarted() {
        return listeners.size() > 0;
    }
}
