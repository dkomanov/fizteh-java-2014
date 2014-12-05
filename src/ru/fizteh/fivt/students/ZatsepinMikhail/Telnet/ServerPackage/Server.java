package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private ServerSocket socket;
    private boolean started;

    public Server() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean startServer(int port) {
        try {
            socket = new ServerSocket(port);
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            });
            listenThread.start();
            started = true;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean stopServer() {
        return false;
    }

    public void listen() {
        try {
            socket.accept();
        } catch (IOException e) {

        }
    }
}
