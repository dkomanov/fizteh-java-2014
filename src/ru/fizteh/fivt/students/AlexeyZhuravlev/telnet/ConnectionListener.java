package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class ConnectionListener extends Thread {

    ServerSocket socket;
    List<ClientCommunicator> connections;
    TableProvider provider;

    ConnectionListener(ServerSocket server, TableProvider passedProvider) {
        socket = server;
        connections = new ArrayList<>();
        provider = passedProvider;
    }

    @Override
    public void run() {
        boolean finished = false;
        while (!finished) {
            try {
                Socket communication = socket.accept();
                ClientCommunicator community = new ClientCommunicator(communication, provider);
                community.start();
                connections.add(community);
            } catch (IOException e) {
                finished = true;
                for (ClientCommunicator thread: connections) {
                    thread.serverShutdown();
                }
            }
        }
    }
}
