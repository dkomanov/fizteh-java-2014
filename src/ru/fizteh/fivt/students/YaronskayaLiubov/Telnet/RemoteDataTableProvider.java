package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 22.12.14.
 */
public class RemoteDataTableProvider implements RemoteTableProvider {
    private boolean connected = false;
    private boolean isServer = false;

    private ServerThread server;
    private String host;
    private int port;

    private DataOutputStream outputStream;
    private Scanner scanner;
    private Shell shell;

    public void setShell(Shell shell) {
        this.shell = shell;
    }


    public RemoteDataTableProvider(StoreableDataTableProvider provider) {
        this.provider = provider;
    }



    public StoreableDataTable getOpenTable() {
        return provider.currTable;
    }

    public void setOpenTable(StoreableDataTable table) {
        provider.currTable = table;
    }
    private Socket socket;

    private StoreableDataTableProvider provider;

    public boolean isLocal() {
        return !connected && !isServer;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isServer() {
        return isServer;
    }



    private void sendMessage(String string) throws IOException {
        try {
            outputStream.writeUTF(string);
            outputStream.writeUTF("\n");
            outputStream.flush();
        } catch (IOException e) {
            connected = false;
            throw new IOException(e.getMessage());
        }
    }

    public String sendCmd(String cmd) throws IOException {
        if (connected) {
            sendMessage(cmd);
            return getMessage();
        } else {
            throw new IOException("on server mode");
        }
    }

    private String getMessage() throws IOException {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        throw new IOException("empty input");
    }

    public String getHost() {
        return host;
    }

    protected int getPort() {
        return port;
    }

    public void start(int port) throws IOException {
        if (port <= 1024 || port >= 65536) {
            throw new ServerRuntimeException("illegal port number");
        }

        this.port = port;
        server = new ServerThread(port, shell);
        isServer = true;
        server.start();
    }

    public void stop() {

        if (isServer()) {
            server.close();
            server.interrupt();
            isServer = false;
        }

    }

    public List<String> getClient() {
        return server.getClientAddress();
    }

    public void connect(String host, int port) {
        if (port <= 1024 || port >= 65536) {
            throw new ClientRuntimeException("illegal port number");
        }

        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new ClientRuntimeException(e.getMessage());
        }
        connected = true;
    }

    public void disconnect() {

        try {
            socket.close();
        } catch (IOException e) {
            throw new ClientRuntimeException(e.getMessage());
        }
        connected = false;
    }

    @Override
    public void close() throws IOException {
        if (isConnected()) {
            disconnect();
        }
        if (isServer()) {
            stop();
        }
        provider.close();

    }

    @Override
    public Table getTable(String name) {
        return provider.getTable(name);
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return provider.createTable(name, columnTypes);
    }

    @Override
    public void removeTable(String name) throws IOException {
        provider.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return provider.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return provider.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return provider.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return provider.createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return provider.getTableNames();
    }
    public Map<String, StoreableDataTable> getTables() {
        return provider.getTables();
    }
}
