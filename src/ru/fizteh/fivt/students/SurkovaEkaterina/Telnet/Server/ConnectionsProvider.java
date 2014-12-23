package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionsProvider extends Thread {
    TelnetServer server;
    ServerSocket socket;
    List<ConnectedClient> clients;

    ConnectionsProvider(ServerSocket socket, TelnetServer server) {
        this.server = server;
        this.socket = socket;
        this.clients = new ArrayList<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket.setSoTimeout(100);
                Socket newConnection = socket.accept();
                ConnectedClient client = new ConnectedClient(newConnection, server);
                client.start();
                this.clients.add(client);
            } catch (IOException e) {
                for (ConnectedClient client: clients) {
                    client.closeConnection();
                }
            }
        }
    }
}
