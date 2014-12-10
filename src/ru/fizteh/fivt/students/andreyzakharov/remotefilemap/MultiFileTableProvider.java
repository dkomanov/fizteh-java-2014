package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.*;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands.CommandRunner;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private final int CONNECTION_LIMIT = 50;

    private ExecutorService clientPool = Executors.newFixedThreadPool(CONNECTION_LIMIT);
    private Map<Integer, Host> clientMap = new ConcurrentHashMap<>();

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

    class ConnectionAccepter implements Runnable {
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    clientPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException e) {
                System.err.println("not started: " + e.getMessage());
            }
        }
    }

    class ClientTask implements Runnable {
        Socket socket;

        ClientTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            clientMap.put(this.hashCode(),
                    new Host(socket.getInetAddress().toString().replace("/", ""), socket.getPort()));
            byte buffer[] = new byte[64*1024];
            try {
                while (true) {
                    int len = socket.getInputStream().read(buffer);
                    if (len == -1) {
                        break;
                    }
                    String request = new String(buffer, 0, len, "UTF-8");
                    String reply;
                    try {
                        reply = MultiFileTableProvider.this.run(request);
                        socket.getOutputStream().write(reply.getBytes("UTF-8"));
                    } catch (CommandInterruptException e) {
                        reply = "remote: " + e.getMessage();
                        socket.getOutputStream().write(reply.getBytes("UTF-8"));
                    }
                }
            } catch (IOException e) {
                /*System.err.println("Connection from " + socket.getRemoteSocketAddress()
                        + " failed: " + e.getMessage());*/
            }
            clientMap.remove(this.hashCode());
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
                    // suppress the exception
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
        StringBuilder result = new StringBuilder();
        for (String command : commands.split("\\s*[;\\n]\\s*")) {
            if (status == ProviderStatus.CONNECTED && !commandRunner.isLocal(command)) {
//                result.append("@REMOTE: ");
                try {
                    outgoingSocket.getOutputStream().write(command.getBytes("UTF-8"));
                    byte buffer[] = new byte[64 * 1024];
                    int len = outgoingSocket.getInputStream().read(buffer);
                    if (len != -1) {
                        result.append(new String(buffer, 0, len, "UTF-8")).append('\n');
                    } else {
                        finalizeConnection();
                        return result.length() == 0 ? "" : result.substring(0, result.length()-1);
                    }
                } catch (IOException e) {
                    finalizeConnection();
                    result.append("connection: ").append(e.getMessage());
                    return result.toString();
                }
            } else {
//                result.append("@LOCAL: ");
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

    public void startServer(int port) throws IllegalArgumentException {
        if (port < 0 || port > 65536) {
            throw new IllegalArgumentException("connection: invalid port number");
        }
        if (status == ProviderStatus.LOCAL) {
            status = ProviderStatus.SERVER;
            this.port = port;
            new Thread(new ConnectionAccepter()).start();
        }
    }

    public Collection<Host> getUsers() {
        if (status == ProviderStatus.SERVER) {
            return clientMap.values();
        } else {
            return null;
        }
    }

    public void connect(String host, int port) throws IllegalArgumentException, ConnectionInterruptException {
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
        if (status == ProviderStatus.CONNECTED) {
            status = ProviderStatus.LOCAL;
            try {
                outgoingSocket.close();
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: " + e.getMessage());
            }
        }
    }

    private void finalizeConnection() {
        if (status == ProviderStatus.CONNECTED) {
            try {
                status = ProviderStatus.LOCAL;
                outgoingSocket.close();
            } catch (IOException e) {
                //
            }
        }
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public String getHost() {
        return status == ProviderStatus.CONNECTED ? host : null;
    }

    public int getPort() {
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
