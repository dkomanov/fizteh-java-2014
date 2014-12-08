package ru.fizteh.fivt.students.gudkov394.telnet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public String getHost() {
        return socket.getInetAddress().toString();
    }

    public int getPort() {
        return socket.getPort();
    }

    public void connect(String host, int port) throws IOException {
        if (socket != null && socket.isConnected()) {
            throw new IOException("already connected");
        }
        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public int disconnect() throws IOException {
        if (socket == null || !socket.isConnected()) {
            return 0;
        }
        socket.close();
        return 1;
    }

    public String whereAmI() {
        if (socket == null || !socket.isConnected()) {
            return "local";
        }
        return "remote" + socket.getInetAddress() + socket.getPort();
    }

    public void send(String s) throws IOException {
        out.writeUTF(s);
        out.flush();
    }

    public String read() throws IOException {
        return in.readUTF();
    }
}
