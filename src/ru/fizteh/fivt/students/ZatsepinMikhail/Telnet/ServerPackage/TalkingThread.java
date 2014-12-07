package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TalkingThread extends Thread {
    private Socket client;
    private TableProvider dataBase;

    public TalkingThread(Socket newClient, TableProvider newDataBase) {
        client = newClient;
        dataBase = newDataBase;
    }

    public String getClientName() {
        return client.getRemoteSocketAddress().toString();
    }

    @Override
    public void run() {
        try {
            Scanner input = new Scanner(client.getInputStream());
            PrintStream output = new PrintStream(client.getOutputStream());
            System.err.println("i am talking");
            while (!client.isClosed() & input.hasNext()) {
                String message = input.nextLine();

            }
        } catch (IOException e) {
            //
        }

    }
}
