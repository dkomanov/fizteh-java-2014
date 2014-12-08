package ru.fizteh.fivt.students.irina_karatsapova.proxy.server;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.ClientAcceptorThread;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionsManagerThread extends Thread {
    ServerSocket serverSocket;
    TableProvider tableProvider;
    public List<ClientAcceptorThread> clientAcceptors = new ArrayList<>();

    public ConnectionsManagerThread(ServerSocket serverSocket, TableProvider tableProvider) {
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
                    ClientAcceptorThread clientAcceptorThread = new ClientAcceptorThread(clientSocket, tableProvider);
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
        for (Thread thread: clientAcceptors) {
            thread.interrupt();
        }
        for (Thread thread: clientAcceptors) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // it will never happen
            }
        }
    }
}
