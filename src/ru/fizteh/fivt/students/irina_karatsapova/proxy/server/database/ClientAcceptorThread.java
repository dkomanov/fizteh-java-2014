package ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAcceptorThread extends Thread {
    public Socket clientSocket;
    TableProvider tableProvider;

    public ClientAcceptorThread(Socket socket, TableProvider tableProvider) {
        clientSocket = socket;
        this.tableProvider = tableProvider;
    }

    @Override
    public void run() {
        try {
            BufferedReader in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            InterpreterDatabase interpreter = new InterpreterDatabase(in, out);
            InterpreterStateDatabase state = new InterpreterStateDatabase(tableProvider, out);
            interpreter.interactiveMode(state);
            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Exception in thread");
        }
    }
}
