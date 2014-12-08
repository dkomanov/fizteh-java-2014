package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.*;
import java.net.ServerSocket;
import java.util.List;

public class Server {
    private ServerSocket server;
    private boolean started;
    private ListenThread listener;
    private int activePort;
    private TableProvider dataBase;

    public Server(TableProvider newDataBase) {
        started = false;
        dataBase = newDataBase;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean startServer(int port) {
        try {
            server = new ServerSocket(port);
            activePort = port;
            listener = new ListenThread(server, dataBase);
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

    public void stopServer() throws IOException {
        listener.stopExecution();
        started = false;
    }

    public List<String> getUserList() {
        return listener.getClients();
    }

}
