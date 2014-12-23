package ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Server;

import ru.fizteh.fivt.students.SurkovaEkaterina.Telnet.Shell.CommandsParser;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ConnectedClient extends Thread {
    Socket socket;
    TelnetServer server;
    volatile boolean serverWorks;

    ConnectedClient(Socket socket, TelnetServer server) {
        this.socket = socket;
        this.server = server;
        this.serverWorks = true;
    }

    public void closeConnection() {
       try {
           socket.close();
           serverWorks = false;
       } catch (IOException e) {
           // cannot close socket
       }
    }

    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(socket.getOutputStream());
            ServerShell shell = new ServerShell(server, socket.getInputStream(), out);
            while (serverWorks) {
                String command = shell.socketNextCommand();
                String[] commands = CommandsParser.parseCommands(command);
                if (command.length() == 0) {
                    continue;
                }
                for (String currentCommand:commands) {
                    if (!shell.execute(currentCommand)) {
                        break;
                    }
                }
            }
            closeConnection();
        } catch (IOException | NoSuchElementException e) {
            closeConnection();
        }
    }
}
