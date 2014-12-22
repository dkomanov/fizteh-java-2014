package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

/**
 * @author AlexeyZhuravlev
 */
public class ConnectionsCloser extends Thread {

    ServerLogic server;
    ClientLogic client;

    ConnectionsCloser(ServerLogic passedServer, ClientLogic passedClient) {
        server = passedServer;
        client = passedClient;
    }

    @Override
    public void run() {
        try {
            while (server.isStarted()) {
                server.stop();
            }
            if (client.isConnected()) {
                client.disconnect();
            }
        } catch (Exception e) {
            //do nothing
        }
    }
}
