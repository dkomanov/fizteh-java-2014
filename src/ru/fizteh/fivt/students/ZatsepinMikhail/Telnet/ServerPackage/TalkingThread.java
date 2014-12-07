package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TalkingThread extends Thread {
    private Socket client;

    public TalkingThread(Socket client) {
        this.client = client;
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
                System.out.println(message);
                output.print("get message!");
            }
        } catch (IOException e) {
            //
        }

    }
}
