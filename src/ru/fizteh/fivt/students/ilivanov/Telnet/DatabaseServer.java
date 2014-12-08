package ru.fizteh.fivt.students.ilivanov.Telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseServer {
    private FileMapProvider database;
    private boolean started;
    private Thread serverThread;
    private ServerHandler server;
    private int port;

    public DatabaseServer(FileMapProvider provider) {
        database = provider;
        started = false;
    }

    void start(int port) throws InterruptedException, ServerStartException {
        Object lock = new Object();
        server = new ServerHandler(port, lock);
        synchronized (lock) {
            serverThread = new Thread(server);
            serverThread.start();
            while (server.state == ServerStartState.NOT_SET) {
                lock.wait();
            }
        }
        if (server.state != ServerStartState.STARTED) {
            serverThread.join();
            started = false;
            throw new ServerStartException(server.message);
        }
        started = true;
        this.port = port;
    }

    int stop() throws InterruptedException {
        server.stop = true;
        serverThread.join();
        started = false;
        return port;
    }

    boolean isStarted() {
        return started;
    }

    String[] listConnections() {
        if (!started) {
            throw new IllegalStateException("Server not started");
        }
        server.connectionLock.readLock().lock();
        try {
            String[] result = new String[server.connections.size()];
            int index = 0;
            for (Map.Entry<Integer, ClientHandler> entry : server.connections.entrySet()) {
                result[index] = String.format("%s:%d",
                        entry.getValue().socket.getInetAddress().getHostAddress(),
                        entry.getValue().socket.getPort());
                index++;
            }
            return result;
        } finally {
            server.connectionLock.readLock().unlock();
        }
    }

    class ServerStartException extends Exception {
        ServerStartException(String message) {
            super(message);
        }
    }

    enum ServerStartState {
        NOT_SET,
        STARTED,
        FAILED
    }

    class ServerHandler implements Runnable {
        int port;
        ServerStartState state;
        Object lock;
        String message;
        boolean stop;
        HashMap<Integer, ClientHandler> connections;
        ReadWriteLock connectionLock;

        public ServerHandler(int port, Object lock) {
            this.port = port;
            this.lock = lock;
            connectionLock = new ReentrantReadWriteLock();
            state = ServerStartState.NOT_SET;
            message = "undefined error";
            stop = false;
            connections = new HashMap<>();
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                synchronized (lock) {
                    state = ServerStartState.STARTED;
                    lock.notify();
                }
                serverSocket.setSoTimeout(1000);
                ExecutorService service = Executors.newCachedThreadPool();
                while (!stop) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler socketHandler = new ClientHandler(socket);
                        connectionLock.writeLock().lock();
                        try {
                            connections.put(socket.getLocalPort(), socketHandler);
                        } finally {
                            connectionLock.writeLock().unlock();
                        }
                        service.submit(socketHandler);
                    } catch (SocketTimeoutException e) {
                        //All is well check stop value
                    }
                }
                try {
                    service.shutdown();
                    if (!service.awaitTermination(5, TimeUnit.SECONDS)) {
                        service.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    service.shutdownNow();
                }
                connectionLock.writeLock().lock();
                try {
                    for (Map.Entry<Integer, ClientHandler> entry : connections.entrySet()) {
                        if (!entry.getValue().socket.isClosed()) {
                            try {
                                entry.getValue().socket.close();
                            } catch (IOException e) {
                                //Unable to close
                            }
                        }
                    }
                    connections.clear();
                } finally {
                    connectionLock.writeLock().unlock();
                }
            } catch (IOException e) {
                synchronized (lock) {
                    state = ServerStartState.FAILED;
                    message = "Unable to create socket server";
                    lock.notify();
                }
            } catch (IllegalArgumentException e) {
                synchronized (lock) {
                    state = ServerStartState.FAILED;
                    message = "Port number out of range";
                    lock.notify();
                }
            }
        }
    }

    class ClientHandler implements Runnable {
        Socket socket;
        ServerHandler server;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream writer = new PrintStream(socket.getOutputStream());
                Shell shell = new Shell(writer);
                shell.setGreeting("");
                ShellBigBoss databaseHandler = new ShellBigBoss(database);
                databaseHandler.integrate(shell);
                shell.run(reader);
            } catch (IOException e) {
                //Child thread do nothing
            } finally {
                int port = socket.getLocalPort();
                try {
                    socket.close();
                } catch (IOException e) {
                    //No action required
                }
                server.connectionLock.writeLock().lock();
                try {
                    server.connections.remove(port);
                } finally {
                    server.connectionLock.writeLock().unlock();
                }
            }
        }
    }
}
