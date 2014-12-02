package ru.fizteh.fivt.students.andreyzakharov.remotefilemap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiFileTableProvider implements AutoCloseable, TableProvider {
    private Path dbRoot;
    private Map<String, MultiFileTable> tables;
    private MultiFileTable activeTable;
    private TableEntrySerializer serializer = new TableEntryJsonSerializer();
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private boolean closed;

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
