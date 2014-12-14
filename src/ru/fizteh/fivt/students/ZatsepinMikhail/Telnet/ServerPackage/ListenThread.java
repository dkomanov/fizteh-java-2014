package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListenThread extends Thread {
    ServerSocket server;
    HashSet<TalkingThread> clients;
    TableProvider dataBase;
    boolean started;

    public ListenThread(ServerSocket newServer, TableProvider newDataBase) {
        server = newServer;
        clients = new HashSet<>();
        dataBase = newDataBase;
    }

    @Override
    public void run() {
        started = true;
        while (started) {
            try {
                Socket client = server.accept();
                TalkingThread newClientThread = new TalkingThread(client, dataBase);
                clients.add(newClientThread);
                newClientThread.start();
            } catch (IOException e) {
                //
            }
        }
    }

    public List<String> getClients() {
        clients.removeIf((TalkingThread t)->!t.isAlive());
        List<String> result = new ArrayList<>();
        for (TalkingThread oneClient : clients) {
            result.add(oneClient.getClientName());
        }
        return result;
    }

    public void stopExecution() {
        for (TalkingThread client : clients) {
            client.stopExecution();
        }
        try {
            server.close();
            started = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
