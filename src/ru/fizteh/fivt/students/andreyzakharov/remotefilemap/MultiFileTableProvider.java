package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands.CommandRunner;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MultiFileTableProvider implements AutoCloseable, RemoteTableProvider {
    private Path dbRoot;
    private Map<String, MultiFileTable> tables;
    private MultiFileTable activeTable;
    private TableEntrySerializer serializer = new TableEntryJsonSerializer();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private boolean closed;
    private CommandRunner commandRunner = new CommandRunner();
    private Socket outgoingSocket;

    private ProviderStatus status = ProviderStatus.LOCAL;
    private String host = null;
    private int port = -1;

    private List<ClientHandler> threadList;
    private ConnectionAccepter serverThread;

    public class Host {
        public String host;
        public int port;

        public Host(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    public enum ProviderStatus {
        LOCAL,
        SERVER,
        CONNECTED
    }

    class ConnectionAccepter extends Thread implements Closeable {
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                while (!Thread.interrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clientHandler.start();
                }
            } catch (IOException e) {
                for (ClientHandler clientHandler : threadList) {
                    try {
                        clientHandler.close();
                        clientHandler.interrupt();
                    } catch (IOException e1) {
                        // continue...
                    }
                }
            }
        }

        @Override
        public void close() throws IOException {
            serverSocket.close();
        }
    }

    class ClientHandler extends Thread implements Closeable {
        Socket socket;
        Host host;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public Host getHost() {
            return host;
        }

        @Override
        public void run() {
            try {
                host = new Host(socket.getInetAddress().toString().replace("/", ""), socket.getPort());
                byte[] buffer = new byte[64 * 1024];
                threadList.add(this);
                while (!Thread.interrupted()) {
                    int len = socket.getInputStream().read(buffer);
                    if (len == -1) {
                        break;
                    }
                    String request = new String(buffer, 0, len, "UTF-8");
                    String reply = "";
                    try {
                        reply = MultiFileTableProvider.this.run(request);
                    } catch (CommandInterruptException e) {
                        reply = "remote: " + e.getMessage();
                    } finally {
                        socket.getOutputStream().write(reply.getBytes("UTF-8"));
                    }
                }
            } catch (IOException e) {
                // shutdown
            } finally {
                threadList.remove(this);
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    public MultiFileTableProvider(Path dbPath) throws ConnectionInterruptException {
        if (!Files.exists(dbPath)) {
            try {
                Files.createDirectory(dbPath);
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: destination does not exist, can't be created");
            }
        }
        if (!Files.isDirectory(dbPath)) {
            throw new ConnectionInterruptException("connection: destination is not a directory");
        }
        dbRoot = dbPath;
        open();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + dbRoot.toAbsolutePath().normalize().toString() + "]";
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            if (tables != null) {
                try {
                    for (MultiFileTable table : tables.values()) {
                        table.unload();
                    }
                } catch (ConnectionInterruptException e) {
                    // shutdown
                }
            }
        }
    }

    private void checkClosed() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("connection: connection was closed");
        }
    }

    public String run(String commands) throws CommandInterruptException {
        checkClosed();
        StringBuilder result = new StringBuilder();
        for (String command : commands.split("\\s*[;\\n]\\s*")) {
            if (status == ProviderStatus.CONNECTED && !commandRunner.isLocal(command)) {
                try {
                    outgoingSocket.getOutputStream().write(command.getBytes("UTF-8"));
                    byte[] buffer = new byte[64 * 1024];
                    int len = outgoingSocket.getInputStream().read(buffer);
                    if (len != -1) {
                        result.append(new String(buffer, 0, len, "UTF-8")).append('\n');
                    } else {
                        finalizeConnection();
                        return result.length() == 0 ? "" : result.substring(0, result.length() - 1);
                    }
                } catch (IOException e) {
                    finalizeConnection();
                    result.append("connection: ").append(e.getMessage());
                    return result.toString();
                }
            } else {
                try {
                    result.append(commandRunner.run(this, command));
                } catch (CommandInterruptException e) {
                    result.append(e.getMessage());
                }
                return result.toString();
            }
        }
        return result.length() == 0 ? "" : result.substring(0, result.length() - 1);
    }

    public void start(int port) throws IllegalArgumentException {
        checkClosed();
        if (port < 0 || port > 65536) {
            throw new IllegalArgumentException("connection: invalid port number");
        }
        if (status == ProviderStatus.LOCAL) {
            status = ProviderStatus.SERVER;
            this.port = port;
            threadList = new ArrayList<>();
            serverThread = new ConnectionAccepter();
            serverThread.start();
//            clientPool = Executors.newFixedThreadPool(connectionLimit + 1);
//            clientPool.submit(new ConnectionAccepter());
        }
    }

    public int stop() throws IOException {
        checkClosed();
        if (status == ProviderStatus.SERVER) {
            status = ProviderStatus.LOCAL;
            serverThread.close();
            serverThread.interrupt();
            return port;
        }
        return -1;
    }

    public Collection<Host> getUsers() {
        checkClosed();
        if (status == ProviderStatus.SERVER) {
            return threadList.stream().map(ClientHandler::getHost).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public void connect(String host, int port) throws IllegalArgumentException, ConnectionInterruptException {
        checkClosed();
        if (port < 0 || port > 65536) {
            throw new IllegalArgumentException("connection: invalid port number");
        }
        if (status == ProviderStatus.LOCAL) {
            status = ProviderStatus.CONNECTED;
            this.port = port;
            this.host = host;
            try {
                outgoingSocket = new Socket(host, port);
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: " + e.getMessage());
            }
        }
    }

    public void disconnect() throws ConnectionInterruptException {
        checkClosed();
        if (status == ProviderStatus.CONNECTED) {
            status = ProviderStatus.LOCAL;
            try {
                if (outgoingSocket != null) {
                    outgoingSocket.close();
                }
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: " + e.getMessage());
            }
        }
    }

    private void finalizeConnection() {
        if (status == ProviderStatus.CONNECTED) {
            status = ProviderStatus.LOCAL;
            try {
                outgoingSocket.close();
            } catch (IOException e) {
                System.err.println("exception on close: " + e.getMessage());
            }
        }
    }

    public ProviderStatus getStatus() {
        checkClosed();
        return status;
    }

    public String getHost() {
        checkClosed();
        return status == ProviderStatus.CONNECTED ? host : null;
    }

    public int getPort() {
        checkClosed();
        return status == ProviderStatus.CONNECTED ? port : -1;
    }

    @Override
    public Table getTable(String name) {
        checkClosed();
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        lock.readLock().lock();
        Table table = tables.get(name);
        lock.readLock().unlock();
        return table;
    }

    public MultiFileTable getCurrent() {
        checkClosed();
        return activeTable;
    }

    public Map<String, MultiFileTable> getAllTables() {
        checkClosed();
        lock.readLock().lock();
        Map<String, MultiFileTable> tables = this.tables;
        lock.readLock().unlock();
        return tables;
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException {
        checkClosed();
        if (name == null || columnTypes == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty() || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        lock.writeLock().lock();
        Table table;
        Path path = dbRoot.resolve(name);
        if (!tables.containsKey(name)) {
            if (Files.exists(path)) {
                if (!Files.isDirectory(path)) {
                    lock.writeLock().unlock();
                    throw new IOException("connection: destination is not a directory");
                }
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                    if (stream.iterator().hasNext()) {
                        lock.writeLock().unlock();
                        throw new IOException("connection: destination is not empty");
                    }
                }
            }
            try {
                tables.put(name, new MultiFileTable(dbRoot.resolve(name), columnTypes, serializer));
            } catch (ConnectionInterruptException e) {
                lock.writeLock().unlock();
                throw new IOException("connection: " + e.getMessage());
            }
            table = tables.get(name);
        } else {
            table = null;
        }
        lock.writeLock().unlock();
        return table;
    }

    @Override
    public void removeTable(String name) {
        checkClosed();
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        lock.writeLock().lock();
        MultiFileTable table = tables.get(name);
        if (table != null) {
            if (activeTable == table) {
                activeTable = null;
            }
            tables.remove(name);
            try {
                table.delete();
            } catch (ConnectionInterruptException e) {
                //
            }
        } else {
            lock.writeLock().unlock();
            throw new IllegalStateException("table does not exist");
        }
        lock.writeLock().unlock();
    }

    public void useTable(String name) throws IllegalStateException {
        checkClosed();
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        lock.writeLock().lock();
        MultiFileTable table = tables.get(name);
        if (table != null) {
            activeTable = table;
        } else {
            lock.writeLock().unlock();
            throw new IllegalStateException("table does not exist");
        }
        lock.writeLock().unlock();
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        checkClosed();
        if (table == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }
        return serializer.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        checkClosed();
        if (table == null || value == null) {
            throw new IllegalArgumentException("null argument");
        }
        return serializer.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) {
        checkClosed();
        if (table == null) {
            throw new IllegalArgumentException("null argument");
        }
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new TableEntry(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
        checkClosed();
        if (table == null || values == null) {
            throw new IllegalArgumentException("null argument");
        }
        List<Object> objValues = new ArrayList<>(values);
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (objValues.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("invalid column type");
            }
        }
        return new TableEntry(objValues);
    }

    @Override
    public List<String> getTableNames() {
        checkClosed();
        return new ArrayList<>(tables.keySet());
    }

    public void open() throws ConnectionInterruptException {
        checkClosed();
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        MultiFileTable table = new MultiFileTable(file, serializer);
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: unable to load the database");
            }
        }
    }
}
