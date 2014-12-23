package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luba_yaronskaya on 20.12.14.
 */
public class ServerThread extends Thread {
    private int port;
    private ServerSocket serverSocket;
    private StoreableDataTableProvider provider;
    private List<ClientThread> clientsList = new ArrayList<>();
    private Shell shell;

    public ServerThread(int port, Shell shell) throws IOException {
        this.port = port;
        this.shell = shell;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public String getHost() {
        return serverSocket.getInetAddress().toString();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Socket clientSocket = serverSocket.accept();
                ClientThread client = new ClientThread(clientSocket, shell);
                client.start();
                clientsList.add(client);
            }
        } catch (IOException e) {
            for (ClientThread client : clientsList) {
                client.close();
                client.interrupt();
            }

        } finally {
            for (ClientThread client : clientsList) {
                client.close();
                client.interrupt();
            }
        }
    }

    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            throw new ServerRuntimeException(e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

    public List<String> getClientAddress() {
        List<String> res = new ArrayList<>();
        for (ClientThread client : clientsList) {
            String host = client.getHost();
            res.add(client.getHost().replace("/", "") + ":" + client.getPort());
        }
        return res;
    }


}
