package ru.fizteh.fivt.students.irina_karatsapova.proxy.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class InterpreterState {
    public PrintWriter out;
    public boolean connected = false;
    public String host;
    public int port;
    public Socket socket;
    public BufferedReader fromServerStream;
    public PrintWriter toServerStream;

    public InterpreterState(PrintWriter out) {
        this.out = out;
    }

    public void connect(String host, int port, Socket socket) throws Exception {
        try {
            this.host = host;
            this.port = port;
            this.socket = socket;
            connected = true;
            fromServerStream  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toServerStream = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new Exception("Can't create streams between client and server");
        }
    }

    public void disconnect() throws Exception {
        try {
            toServerStream.println("exit");
            fromServerStream.close();
            toServerStream.close();
            socket.close();
            connected = false;
        } catch (IOException e) {
            throw new Exception("Can't disconnect");
        }
    }
}
