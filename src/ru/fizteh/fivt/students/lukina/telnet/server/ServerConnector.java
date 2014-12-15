package ru.fizteh.fivt.students.lukina.telnet.server;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ServerConnector extends Thread {
    ServerSocket serverSocket;
    TableProvider tableProvider;
    private List<ServerAcceptor> clientAcceptors = new ArrayList<>();

    public ServerConnector(ServerSocket serverSocket, TableProvider tableProvider) {
        this.serverSocket = serverSocket;
        this.tableProvider = tableProvider;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    serverSocket.setSoTimeout(100);
                    Socket clientSocket = serverSocket.accept();
                    ServerAcceptor clientAcceptorThread = new ServerAcceptor(clientSocket, tableProvider);
                    clientAcceptors.add(clientAcceptorThread);
                    clientAcceptorThread.start();
                } catch (SocketTimeoutException e) {
                    // just to check whether it was interrupted
                }
            }
        } catch (IOException e) {
            System.out.println("Can't accept");
            System.exit(-1);
        }
        for (Thread thread : clientAcceptors) {
            thread.interrupt();
        }
        for (Thread thread : clientAcceptors) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // it will never happen
            }
        }
    }

    public List<ServerAcceptor> getClientAcceptors() {
        return clientAcceptors;
    }
}

