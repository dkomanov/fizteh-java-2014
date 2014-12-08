package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.Shell;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class RemoteDataBaseTableProvider implements RemoteTableProvider{
    DataBaseTableProvider localProvider;

    public boolean isGuested() {
        return guested;
    }

    boolean guested = false;
    String host;
    int port;

    ServerSocket asyncChannel;
    Socket socketServerChannel;
    Socket socketClientChannel;


    Scanner scanner;
    PrintStream outputStream;
    Shell shell;
    List<ServerListener> listeners;

    public RemoteDataBaseTableProvider(DataBaseTableProvider provider) {
        localProvider = provider;
        shell = new Shell();
        shell.setProvider(this);
    }

    public RemoteDataBaseTableProvider(DataBaseTableProvider provider, String host, int port) throws IOException {
        localProvider = provider;
        try {
            shell = new Shell();
            socketClientChannel =  new Socket();
            InetSocketAddress address = new InetSocketAddress(host, port);
            socketClientChannel.connect(address);
            shell.setProvider(this);

            //catch exception
        } catch (IOException e) {
            throw new IOException("socket hasn't opened");
        }
        guested = true;
    }

git
    public String connect(String host, int port) throws IOException {
        if (isGuested()) {
            return "not connected: already connected";
        } else {
            try {
                socketClientChannel = new Socket();
                InetSocketAddress address = new InetSocketAddress(host, port);
                socketClientChannel.connect(address);
                outputStream = new PrintStream(socketClientChannel.getOutputStream());
                scanner = new Scanner(socketClientChannel.getInputStream());
            } catch (IOException e) {
                throw new IOException("not connected: " + e.getMessage());
            }
            return "connected";
        }
    }

    public String disconnect() throws IOException {
        if (!isGuested()) {
            throw new IOException("not connected");
        }
        socketClientChannel.close();
        guested = false;
        return "disconnected";
    }



    public String startServer() throws IOException, ExecutionException, InterruptedException {
        return startServer(10001);
    }



    public String startServer(int port) throws IOException, ExecutionException, InterruptedException {
        if (guested) {
            throw new IOException("client mode is on");
        }
        asyncChannel = new ServerSocket();
        asyncChannel.bind(new InetSocketAddress(port));
        guested = false;
        ServerListener listener = new ServerListener(shell, asyncChannel);
        listener.start();
        listeners.add(listener);
        return "started at " + port;
    }

    public void waitCommands() {
        while (true) {
            try {
                socketServerChannel = asyncChannel.accept();
                sendMessage(getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getRoot() {
        if (guested) {
           return "remote " + host + ":" + port;
        } else {
            return "local";
        }
    }



    public void sendMessage(String string) throws IOException {
        outputStream.println(string);
    }


    public String getMessage() throws IOException {
        while (!scanner.hasNext()) {
            //wait for stream
        }
        return scanner.next();
    }

    @Override
    public void close() throws IOException {
        if (!guested) {
            try {
                localProvider.close();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
        }
    }


    public String sendCommand(String command) throws IOException {
        if (guested) {
            sendMessage(command);
            return getMessage();
        } else {
            throw new IOException("remote mode on");
        }
    }

    @Override
    public Table getTable(String name) throws IOException {
        if (!guested) {
            return localProvider.getTable(name);
        } else {
            return localProvider.getTable(name);
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        return localProvider.createTable(name, columnTypes);
    }


    @Override
    public void removeTable(String name) throws IOException {
        localProvider.removeTable(name);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        return localProvider.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        return localProvider.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        return localProvider.createFor(table);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        return localProvider.createFor(table, values);
    }

    @Override
    public List<String> getTableNames() {
        return null;
    }


    public DataBaseTable getOpenedTable() {
        if (!guested) {
            return localProvider.getOpenedTable();
        }
        return null;
    }


    public HashMap<String, Integer> getTableList() {
        if (!guested) {
            return localProvider.getTableList();
        }
        return null;
    }
}
