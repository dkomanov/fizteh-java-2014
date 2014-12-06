package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

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
    List<Socket> connections;

    ConnectionListener(ServerSocket server) {
        socket = server;
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean finished = false;
        while (!finished) {
            try {
                Socket communication = socket.accept();
                connections.add(communication);

            } catch (IOException e) {
                finished = true;
            }
        }
    }
}
