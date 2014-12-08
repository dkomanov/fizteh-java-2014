package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class TalkingThread extends Thread {
    private Socket client;
    private TableProvider dataBase;
    Scanner input;
    PrintStream output;

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
            input = new Scanner(client.getInputStream());
            output = new PrintStream(client.getOutputStream());
            System.err.println("i am talking");
            CommandExecutor executor = new CommandExecutor();
            while (!client.isClosed() & input.hasNext()) {
                String message = input.nextLine();
                executor.run(message, output, dataBase);
            }
            return;
        } catch (IOException e) {
            //
        }
    }

    public void stopExecution() throws IOException {
        output.println("S: server has been stopped!");
        client.close();
    }
}
