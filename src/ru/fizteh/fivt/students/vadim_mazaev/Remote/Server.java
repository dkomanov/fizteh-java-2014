package ru.fizteh.fivt.students.vadim_mazaev.Remote;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.students.vadim_mazaev.DataBase.Helper;

public final class Server implements Closeable {
    private ServerSocket serverSocket;
    private DataBaseState dbState;
    private Set<Connector> connections = new HashSet<>();
    private boolean cleaningUp;

    public Server(DataBaseState dbState) {
        this.dbState = dbState;
    }

    private synchronized boolean hasAlreadyStarted() {
        return serverSocket != null;
    }

    private void checkStarted() {
        if (!hasAlreadyStarted()) {
            throw new IllegalStateException("not started");
        }
    }

    public synchronized void start(int port) throws IOException {
        try {
            if (hasAlreadyStarted()) {
                throw new IllegalStateException("already started");
            }
            serverSocket = new ServerSocket(port, 0, InetAddress.getByName("localhost"));
        } catch (IllegalStateException | IOException e) {
            throw new IOException("not started: " + e.getMessage(), e);
        }

        Thread server = new Thread(this::serverLoop);
        server.setDaemon(true);
        server.setPriority(Thread.NORM_PRIORITY);
        server.start();
    }

    public void start() throws IOException {
        start(Helper.DEFAULT_PORT);
    }

    private void serverLoop() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket;
                Connector connection;
                synchronized (this) {
                    clientSocket = serverSocket.accept();
                    ClientDbState clientDbState = new ClientDbState(dbState.getManager(), new PrintStream(
                            clientSocket.getOutputStream())); // TODO remove
                                                              // this crutch!
                    connection = new Connector(clientSocket, this, clientDbState);
                }
                Thread clientThread = new Thread(connection);
                clientThread.setDaemon(true);
                clientThread.start();

                synchronized (this) {
                    connections.add(connection);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public synchronized int stop() throws IOException {
        checkStarted();
        int port = serverSocket.getLocalPort();
        close();
        return port;
    }

    public synchronized List<String> getListOfConnections() {
        checkStarted();
        List<String> connectionsList = new ArrayList<>();
        for (Connector connection : connections) {
            connectionsList.add(connection.toString());
        }
        return connectionsList;
    }

    public synchronized void removeConnection(Connector connection) {
        if (!cleaningUp) {
            connections.remove(connection);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (hasAlreadyStarted()) {
            cleaningUp = true;
            for (Connector connection : connections) {
                try {
                    connection.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
            cleaningUp = false;
            connections.clear();

            try {
                serverSocket.close();
            } finally {
                serverSocket = null;
            }
        }
    }
}
