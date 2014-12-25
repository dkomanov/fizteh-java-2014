package ru.fizteh.fivt.students.dsalnikov.telnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TCPServer implements Runnable {
    private int port = 10001;
    private boolean isStopped = false;
    private ServerSocket serverSocket;
    private Thread runningThread = null;
    private Set<InetAddress> userMap = new HashSet<>();

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        System.out.println("Server started on port " + port);
        while (!isStopped()) {
            Socket clientSocket;
            try {
                //TODO use threadgroup here
                clientSocket = this.serverSocket.accept();
            } catch (IOException exception) {
                if (isStopped()) {
                    System.out.println("Server stopped");
                    return;
                }
                throw new RuntimeException("Error accepting client connection");
            }
            userMap.add(clientSocket.getInetAddress());
            new Thread(new TCPWorkerRunnable(clientSocket)).start();
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException exception) {
            throw new RuntimeException("Cannot open port", exception);
        }
    }

    private synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException exception) {
            throw new RuntimeException("Error closing server", exception);
        }
    }

    public List<String> listusers() {
        return userMap.stream().map(Object::toString).collect(Collectors.toList());


    }

    private synchronized boolean isStopped() {
        return isStopped;
    }
}
