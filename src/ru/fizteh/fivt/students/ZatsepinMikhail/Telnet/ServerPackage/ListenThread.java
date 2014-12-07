package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ListenThread extends Thread {
    ServerSocket server;
    List<TalkingThread> clients;

    public ListenThread(ServerSocket server) {
        this.server = server;
        clients = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean started = true;
        while (started) {
            System.err.println("i am listening");
            try {
                Socket client = server.accept();
                System.err.println("accept new client: " + client.getRemoteSocketAddress());
                TalkingThread newClientThread = new TalkingThread(client);
                clients.add(newClientThread);
                newClientThread.start();
            } catch (IOException e) {
                //
            }
        }
    }

    public List<String> getClients() {
        List<String> result = new ArrayList<>();
        for (TalkingThread oneClient : clients) {
            result.add(oneClient.getClientName());
        }
        return result;
    }
}
