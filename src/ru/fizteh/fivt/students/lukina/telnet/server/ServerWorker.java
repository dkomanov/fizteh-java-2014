package ru.fizteh.fivt.students.lukina.telnet.server;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.List;


public class ServerWorker {
    private boolean started = false;
    private TableProvider tableProvider;
    private PrintWriter out;
    private ServerSocket serverSocket;
    private int port;
    private ServerConnector connectionsManager;

    public ServerWorker(TableProvider tableProvider, PrintWriter out) {
        this.out = out;
        this.tableProvider = tableProvider;
    }

    public String getAddr() {
        return serverSocket.getLocalSocketAddress().toString();
    }

    private List<ServerAcceptor> getClientAcceptors() {
        return connectionsManager.getClientAcceptors();
    }

    public void startCommand(int port, ServerSocket serverSocket) {
        this.port = port;
        this.serverSocket = serverSocket;
        started = true;
        connectionsManager = new ServerConnector(serverSocket, tableProvider);
        connectionsManager.start();
    }

    public int stopCommand() throws Exception {
        started = false;
        connectionsManager.interrupt();
        try {
            connectionsManager.join();
            serverSocket.close();
        } catch (Exception e) {
            throw new Exception("Error while stopping the server");
        }
        return port;
    }

    public boolean isStarted() {
        return started;
    }

    public void start(String[] args) throws Exception {
        int port = 10001;
        if (started) {
            out.println("already started");
            return;
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                throw new Exception("Port should be a number");
            }
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            out.println("not started: wrong port number or it is already busy");
            return;
        }
        startCommand(port, serverSocket);
        out.println("started at port " + port);
    }

    public void stop() throws Exception {
        if (!started) {
            out.println("not started");
            return;
        }
        int port = stopCommand();
        out.println("stopped at port " + port);
    }

    public void exit() throws Exception {
        if (!started) {
            stopCommand();
        }
        System.exit(0);
    }

    public void listUsers() throws Exception {
        if (!started) {
            out.println("not started");
            return;
        }
        List<ServerAcceptor> clientAcceptors = getClientAcceptors();
        for (ServerAcceptor client : clientAcceptors) {
            if (client.isAlive()) {
                String host = client.clientSocket.getInetAddress().getHostName();
                out.println(host + ":" + port);
            }
        }
    }
}



