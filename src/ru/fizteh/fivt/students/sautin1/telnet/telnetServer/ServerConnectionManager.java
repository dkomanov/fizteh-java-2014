package ru.fizteh.fivt.students.sautin1.telnet.telnetServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.*;

/**
 * Created by sautin1 on 12/16/14.
 */
public class ServerConnectionManager implements Runnable {

    private class ClientInfo {
        private Thread thread;
        private Socket socket;

        private ClientInfo(Thread thread, Socket socket) {
            this.thread = thread;
            this.socket = socket;
        }
    }
    private Set<ClientInfo> clientInfo;
    private int port;

    public ServerConnectionManager(int port) {
        this.port = port;
        clientInfo = new HashSet<ClientInfo>();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Waiting for a client...");
                for (Iterator<ClientInfo> iterator = clientInfo.iterator(); iterator.hasNext(); ) {
                    if (!iterator.next().thread.isAlive()) {
                        iterator.remove();
                    }
                }
                Socket socket = serverSocket.accept();
                System.out.println("Got a client :)");

                ServerClientResponder responder = new ServerClientResponder(socket);
                Thread responderThread = new Thread(responder);
                clientInfo.add(new ClientInfo(responderThread, socket));
                responderThread.start();
            }
        } catch (IOException | ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<Socket> listSockets() {
        List<Socket> socketList = new ArrayList<>();
        for (ClientInfo client : clientInfo) {
            socketList.add(client.socket);
        }
        return socketList;
    }

    public List<Thread> listThreads() {
        List<Thread> threadList = new ArrayList<>();
        for (ClientInfo client : clientInfo) {
            threadList.add(client.thread);
        }
        return threadList;
    }
}
