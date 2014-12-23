package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.TableSystem.DatabaseTableProviderFactory;

import java.io.*;
import java.net.ServerSocket;

public class ServerMain {
    public static ConnectionsProvider connectionsProvider;

    public static void main(String[] args) {
        try {
            String databaseDirectory =
                    System.getProperty("fizteh.db.dir");

            DatabaseTableProviderFactory factory =
                    new DatabaseTableProviderFactory();

            TelnetServer server = new TelnetServer(factory.create(databaseDirectory));
            ServerSocket mainSocket = new ServerSocket();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (server.works()) {
                        try {
                            server.stop();
                        } catch (Exception e) {
                            // cannot stop server
                        }
                    }
                }
            });
            ConnectionsProvider connectionsProvider = new ConnectionsProvider(mainSocket, server);
            connectionsProvider.start();
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            connectionsProvider.interrupt();
            System.exit(1);
        }
    }
}
