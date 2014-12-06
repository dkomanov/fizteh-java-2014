package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server {
    private AsynchronousServerSocketChannel server;
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
            server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
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
            while (true) {
                Future<AsynchronousSocketChannel> acceptFuture = server.accept();
                AsynchronousSocketChannel client = acceptFuture.get();
                Thread talkWithclient = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        talk(client);
                    }
                });
                talkWithclient.start();
            }
        } catch (InterruptedException e) {
            //
        } catch (ExecutionException e) {
            //
        }
    }

    public void talk(AsynchronousSocketChannel client) {
        ByteBuffer bufferForMessage = ByteBuffer.allocate(1024);
        while (client.isOpen()) {
            Future<Integer> message = client.read(bufferForMessage);
            try {
                message.get();
                System.out.println(new String(bufferForMessage.array(), "UTF-8"));
            } catch (InterruptedException e) {
                //
            } catch (ExecutionException e) {
                //
            } catch (UnsupportedEncodingException e) {
                //
            }
            for (int i = 0; i < 1024; ++i) {
                bufferForMessage.put(i, (byte) 0);
            }
        }
        try {
            client.close();
        } catch (IOException e) {

        }
    }
}
