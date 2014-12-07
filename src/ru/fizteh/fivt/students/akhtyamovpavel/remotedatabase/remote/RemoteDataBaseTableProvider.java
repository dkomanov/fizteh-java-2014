package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.DataBaseTableProvider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by akhtyamovpavel on 07.12.14.
 */
public class RemoteDataBaseTableProvider implements RemoteTableProvider{
    DataBaseTableProvider localProvider;
    boolean guested = false;
    String host;
    int port;

    ServerSocketChannel asyncChannel;
    SocketChannel socketChannel;

    public RemoteDataBaseTableProvider(DataBaseTableProvider provider) {
        localProvider = provider;
    }
    public RemoteDataBaseTableProvider(DataBaseTableProvider provider, String host, int port) throws IOException {
        localProvider = provider;
        try {
            socketChannel = SocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            socketChannel.connect(address);

            //catch exception
        } catch (IOException e) {
            throw new IOException("socket hasn't opened");
        }
    }



    public String startServer() throws IOException, ExecutionException, InterruptedException {
        return startServer(10001);
    }



    public String startServer(int port) throws IOException, ExecutionException, InterruptedException {
        if (guested) {
            throw new IOException("client mode is on");
        }
        asyncChannel = ServerSocketChannel.open().bind(new InetSocketAddress(port));
        guested = false;
        boolean accepted = false;

        asyncChannel.accept();

        System.out.println(asyncChannel.getLocalAddress());
        return "started at" + port;
    }

    public String getRoot() {
        if (guested) {
           return "remote " + host + ":" + port;
        } else {
            return "local";
        }
    }



    public void sendMessage(String string) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(string.length());
        try {
            buffer.put(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IOException("encoding error");
        }

        if (socketChannel.write(buffer) != 4 + string.getBytes().length) {
            throw new IOException("writing to socket error");
        }
    }

    public String readString(ByteBuffer buffer, int length) throws IOException {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            try {
                result.append(buffer.getChar());
            } catch (BufferUnderflowException e) {
                throw new IOException("socket error");
            }
        }
        return result.toString();

    }

    public String getMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.read(buffer);
        int messageLength = buffer.getInt();
        return readString(buffer, messageLength);
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

    public void checkRemote() throws IOException {
        if (!guested) {
            throw new IOException("provider in remote mode");
        }
    }

    public String sendCommand(String command) throws IOException {
        if (guested) {
            sendMessage(command);
        }
        return getMessage();
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


}
