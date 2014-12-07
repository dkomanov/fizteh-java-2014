package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> listClients = new ArrayList<>();
    final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public Server() throws IOException {
        serverSocket = new ServerSocket(10001);
        threadPool.submit(listenForClients);
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool.submit(listenForClients);
    }

    public void stop() throws IOException {
        threadPool.shutdown();
        serverSocket.close();
        for (Socket s : listClients) {
            s.close();
        }
        threadPool.shutdown();

    }

    public ArrayList<Socket> listUsers() {
        return listClients;
    }

    private Runnable listenForClients = new Runnable() {
        @Override
        public void run() {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (SocketException e) {
                        return;
                    }
                    listClients.add(clientSocket);
                    threadPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            threadPool.submit(listenForClients);
        }
    };

    private class ClientTask implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientTask(Socket clientSocket) throws IOException {
            socket = clientSocket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                readClient();
            }
        }

        private void readClient() {
            try {
                String s = in.readUTF();
                writeClient(s);
            } catch (IOException e) {
                return;
            }
        }

        private void writeClient(String s) {
            try {
                out.writeUTF(s);
            } catch (IOException e) {
                return;
            }
        }
    }
}