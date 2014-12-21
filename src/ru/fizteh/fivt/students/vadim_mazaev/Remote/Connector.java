package ru.fizteh.fivt.students.vadim_mazaev.Remote;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import ru.fizteh.fivt.students.vadim_mazaev.DataBase.DbTable;
import ru.fizteh.fivt.students.vadim_mazaev.Interpreter.Interpreter;

public final class Connector implements Runnable, Closeable {
    private final Interpreter interpreter;
    private boolean closed;
    private final Socket socket;
    private final Server server;

    public Connector(Socket socket, Server server, ClientDbState dbState) throws IOException {
        this.socket = socket;
        this.server = server;
        PrintStream printer = new PrintStream(socket.getOutputStream());
        interpreter = new Interpreter(dbState, RemoteDbCommands.getCommands(), socket.getInputStream(), printer);
        interpreter.setExitHandler(() -> {
            DbTable link = (DbTable) dbState.getUsedTable();
            if (link != null && (link.getNumberOfUncommittedChanges() > 0)) {
                printer.println(link.getNumberOfUncommittedChanges() + " unsaved changes");
                return false;
            }
            return true;
        });
    }

    @Override
    public String toString() {
        return socket.toString(); // TODO check
    }

    @Override
    public void run() {
        try {
            interpreter.run();
        } catch (Exception e) {
            if (e.getMessage() != null) {
                interpreter.printMessage(e.getMessage());
            } else {
                interpreter.printMessage("Something went wrong. Unexpected error");
                // interpreter.printMessage(e.printStackTrace(s);
            }
            // System.exit(1);
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            socket.close();
            server.removeConnection(this);
        }
    }
}
