package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.util.List;

public class RealRemoteTableProvider implements RemoteTableProvider {
    private SocketChannel server;

    public RealRemoteTableProvider(String hostname, int port) {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 10001);
            SocketChannel server = SocketChannel.open();
            boolean connected = server.connect(serverAddress);
        } catch (IOException e) {
            //supress tmp
        }
    }

    @Override
    public void close() throws IOException {
        server.close();
    }

    @Override
    public Table getTable(String name) {
        return null;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        ByteBuffer message = ByteBuffer.allocate(1000);
        message.put(name.getBytes());
        message.put(columnTypes.toString().getBytes());
        System.out.println(message);
        return null;
    }

    @Override
    public void removeTable(String name) throws IOException {

    }

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
}
