package ru.fizteh.fivt.students.sautin1.telnet.telnetServer;

import ru.fizteh.fivt.students.sautin1.telnet.ShellState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sautin1 on 12/16/14.
 */
public class ServerState extends ShellState {
    private Thread connectionManagerThread;
    private ServerConnectionManager connectionManager;
    private boolean isStarted = false;

    private int port;

    public ServerState(BufferedReader inStream, PrintWriter outStream) {
        super(inStream, outStream);
    }

    public int launchConnectionManager(int port) {
        this.port = port;
        isStarted = true;
        connectionManager = new ServerConnectionManager(port);
        connectionManagerThread = new Thread(connectionManager);
        connectionManagerThread.start();
        return port;
    }

    public int stopConnectionManager() {
        for (Thread thread : connectionManager.listThreads()) {
            thread.interrupt();
        }
        for (Socket socket : connectionManager.listSockets()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        connectionManagerThread.interrupt();
        connectionManagerThread = null;
        isStarted = false;
        return port;
    }

    public boolean isServerStarted() {
        return isStarted;
    }

    public List<InetAddress> listUsers() {
        List<Socket> socketList = connectionManager.listSockets();
        List<InetAddress> userList = socketList.stream().map(Socket::getInetAddress).collect(Collectors.toList());
        return userList;
    }
}
