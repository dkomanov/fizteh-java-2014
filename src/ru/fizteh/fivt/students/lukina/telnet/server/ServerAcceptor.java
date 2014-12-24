package ru.fizteh.fivt.students.lukina.telnet.server;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerAcceptor extends Thread {
    public Socket clientSocket;
    TableProvider tableProvider;

    public ServerAcceptor(Socket socket, TableProvider tableProvider) {
        clientSocket = socket;
        this.tableProvider = tableProvider;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            DBaseParser parser = new DBaseParser(in, out);
            DBaseWorker worker = new DBaseWorker(tableProvider, out);
            parser.interactive(worker);
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Exception in thread");
        }
    }
}
