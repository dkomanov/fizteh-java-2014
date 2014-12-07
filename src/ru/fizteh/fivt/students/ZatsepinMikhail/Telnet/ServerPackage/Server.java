package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket server;
    private boolean started;
    private ListenThread listener;
    private int activePort;

    public Server() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean startServer(int port) {
        try {
            server = new ServerSocket(port);
            activePort = port;
            listener = new ListenThread(server);
            listener.start();
            started = true;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int getActivePort() {
        return activePort;
    }

    public boolean stopServer() {
        return false;
    }

    public List<String> getUserList() {
        return listener.getClients();
    }

}
