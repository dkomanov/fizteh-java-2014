package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RealRemoteTableProvider implements RemoteTableProvider {
    private AsynchronousSocketChannel server;
    private boolean connected;

    public RealRemoteTableProvider(String hostname, int port) {
        try {
            connected = false;
            server = AsynchronousSocketChannel.open();
            Future<Void> connection = server.connect(new InetSocketAddress(hostname, port));
            connection.get();
            connected = true;
        } catch (IOException e) {
            //supress tmp
        } catch (ExecutionException e) {
            //
        } catch (InterruptedException e) {
            //
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
        if (!connected) {
            throw new IllegalStateException("not connected");
        }
        ByteBuffer message = ByteBuffer.allocate(1024);
        message.put("create ".getBytes());
        message.put(name.getBytes());
        message.put(" (".getBytes());
        message.put(TypesUtils.toFileSignature(columnTypes).getBytes());
        message.put(")".getBytes());
        message.put((byte) 13);
        message.put((byte) 10);
        try {
            server.write(message).get();
        } catch (ExecutionException e) {
            System.out.println(e.getMessage());
            //
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            //
        }
        System.out.println(new String(message.array(), "UTF-8"));
        Future<Integer> reading = server.read(message);
        try {
            server.read(message).get();
        } catch (ExecutionException e) {
            System.out.println(e.getMessage());
            //
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            //
        }
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
