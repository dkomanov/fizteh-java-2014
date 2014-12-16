package ru.fizteh.fivt.students.sautin1.telnet.telnetClient;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;

/**
 * Created by sautin1 on 12/15/14.
 */
public class RemoteTableProviderClient implements RemoteTableProvider {
    private final Socket socket;
    private final PrintWriter toServerStream;
    private final BufferedReader fromServerStream;

    public RemoteTableProviderClient(Socket socket) throws IOException {
        this.socket = socket;
        this.toServerStream = new PrintWriter(socket.getOutputStream(), true);
        this.fromServerStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String sendCommand(String command) throws IOException {
        toServerStream.println(command);
        StringBuilder result = new StringBuilder();
        try {
            Thread.sleep(200);
            do {
                String resultPart = fromServerStream.readLine();
                result.append(resultPart + System.getProperty("line.separator"));
            } while (fromServerStream.ready());
        } catch (InterruptedException e) {
            // suppress
        }
        return result.toString();
    }

    public void disconnect() throws IOException {
        toServerStream.println("exit");
        fromServerStream.close();
        toServerStream.close();
        socket.close();
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {}

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return null;
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return null;
    }

    @Override
    public Storeable createFor(Table table) {
        return null;
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return null;
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }

    @Override
    public void close() {}
}
