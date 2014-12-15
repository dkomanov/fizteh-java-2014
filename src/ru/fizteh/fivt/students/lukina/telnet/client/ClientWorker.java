package ru.fizteh.fivt.students.lukina.telnet.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWorker {
    public PrintWriter out;
    public BufferedReader fromServerStream;
    public PrintWriter toServerStream;
    private boolean connected = false;
    private String host;
    private int port;
    private Socket socket;

    public ClientWorker(PrintWriter out) {
        this.out = out;
    }

    public void connect(String[] args) throws Exception {
        if (connected) {
            out.println("not connected: connection has already been established");
            return;
        }
        String host = args[1];
        int port;
        try {
            port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new Exception("Port should be a number");
        }
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            out.println("not connected: server is not listening there");
            return;
        }
        try {
            this.host = host;
            this.port = port;
            this.socket = socket;
            connected = true;
            fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServerStream = new PrintWriter(socket.getOutputStream(), true);
            out.println("connected");
        } catch (IOException e) {
            throw new Exception("Can't create streams between client and server");
        }
    }

    public void disconnect() throws Exception {
        if (!connected) {
            out.println("not connected");
            return;
        }
        try {
            toServerStream.println("exit");
            fromServerStream.close();
            toServerStream.close();
            socket.close();
            connected = false;
            out.println("disconnected");
        } catch (IOException e) {
            throw new Exception("Can't disconnect");
        }
    }

    public void exit() throws Exception {
        if (!connected) {
            System.exit(0);
        }
        this.disconnect();
        System.exit(0);
    }

    public void send(String[] args) throws Exception {
        if (!connected) {
            out.println("not connected");
            return;
        }
        if (socket.isInputShutdown() || socket.isOutputShutdown() || !socket.isConnected() || socket.isClosed()) {
            this.disconnect();
            out.println("Server disconnected. Try to connect again.");
        }
        String commandToServer = Client.concatStrings(args, " ");
        toServerStream.println(commandToServer);
        try {
            Thread.sleep(100);
            do {
                String answerFromServer = fromServerStream.readLine();
                if (answerFromServer == null) {
                    this.disconnect();
                    out.println("Server disconnected. Try to connect again.");
                    return;
                }
                out.println(answerFromServer);
            } while (fromServerStream.ready());
        } catch (IOException e) {
            this.disconnect();
            out.println("Server disconnected. Try to connect again.");
        }
    }

    public void WhereAmI() {
        if (!connected) {
            out.println("local");
        } else {
            out.println("remote " + host + " " + port);
        }
    }

    public boolean isConnected() {
        return connected;
    }

}
