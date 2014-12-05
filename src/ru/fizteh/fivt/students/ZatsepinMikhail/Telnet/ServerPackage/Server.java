package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMapFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocketChannel socket;
    private boolean started;
    private ArrayList<SocketAddress> clients;

    public Server() {
        started = false;
        clients = new ArrayList<>();
    }

    public List<SocketAddress> listUsers() {
        return clients;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean startServer(int port) {
        try {
            socket = ServerSocketChannel.open().bind(new InetSocketAddress(port));
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            });
            listenThread.start();
            started = true;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean stopServer() {
        return false;
    }

    public void listen() {
        try {
            SocketChannel clientChannel = socket.accept();

            clients.add(clientChannel.getRemoteAddress());
            Thread listenThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    talkWithClient(clientChannel);
                }
            });
            listenThread.start();
        } catch (IOException e) {

        }
    }

    public void talkWithClient(SocketChannel client) {
        MFileHashMapFactory factory = new MFileHashMapFactory();

    }
}
