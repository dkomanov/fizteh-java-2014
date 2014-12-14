package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ListenThread extends Thread {
    ServerSocket server;
    List<TalkingThread> clients;
    TableProvider dataBase;
    boolean started;

    public ListenThread(ServerSocket newServer, TableProvider newDataBase) {
        server = newServer;
        clients = new ArrayList<>();
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
        for (ListIterator<TalkingThread> oneClientIterator = clients.listIterator();
             oneClientIterator.hasNext(); ) {
                TalkingThread client = oneClientIterator.next();
                if (!client.isAlive()) {
                    oneClientIterator.remove();
                }
        }
        List<String> result = new ArrayList<>();
        for (TalkingThread oneClient : clients) {
            result.add(oneClient.getClientName());
        }
        return result;
    }

    public void stopExecution() throws IOException {
        for (TalkingThread client : clients) {
            client.stopExecution();
        }
        server.close();
        started = false;
    }
}
