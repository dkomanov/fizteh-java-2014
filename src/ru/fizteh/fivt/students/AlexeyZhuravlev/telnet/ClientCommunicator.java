package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands.TableCommand;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;

/**
 * @author AlexeyZhuravlev
 */
public class ClientCommunicator extends Thread {

    Socket socket;
    TableProvider provider;
    volatile boolean serverAlive;

    ClientCommunicator(Socket passedSocket, TableProvider passedProvider) {
        socket = passedSocket;
        provider = passedProvider;
        serverAlive = true;
    }

    public void serverShutdown() {
        serverAlive = false;
        try {
            socket.close();
        } catch (IOException e) {
            //do nothing
        }
    }

    @Override
    public void run() {
        try {
            SocketCommandGetter in = new SocketCommandGetter(socket.getInputStream());
            PrintStream out = new PrintStream(socket.getOutputStream());
            ShellTableProvider localProvider = new ShellTableProvider(provider);
            while (serverAlive) {
                String command = in.nextCommand();
                try {
                    TableCommand interpret = TableCommand.fromString(command);
                    interpret.execute(localProvider, out);
                } catch (ExitCommandException e) {
                    serverAlive = false;
                } catch (UnknownCommandException e) {
                    out.println("Unknown command");
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }
            serverShutdown();
        } catch (IOException | NoSuchElementException e) {
            serverShutdown();
        }
    }
}
