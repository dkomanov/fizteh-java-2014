package ru.fizteh.fivt.students.irina_karatsapova.proxy.server;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;

import java.io.PrintWriter;
import java.net.ServerSocket;

public class InterpreterStateServer {
    private TableProvider tableProvider;
    public PrintWriter out;
    public boolean started;
    public ServerSocket serverSocket;
    public int port;
    public ConnectionsManagerThread connectionsManager;

    public InterpreterStateServer(TableProvider tableProvider, PrintWriter out) {
        this.out = out;
        this.tableProvider = tableProvider;
    }

    public void start(int port, ServerSocket serverSocket) {
        this.port = port;
        this.serverSocket = serverSocket;
        started = true;
        connectionsManager = new ConnectionsManagerThread(serverSocket, tableProvider);
        connectionsManager.start();
    }

    public int stop() throws Exception {
        started = false;
        connectionsManager.interrupt();
        try {
            connectionsManager.join();
            serverSocket.close();
        } catch (Exception e) {
            throw new Exception("Error while stopping the server");
        }
        return port;
    }
}
