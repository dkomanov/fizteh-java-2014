package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Мирон on 08.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class CTableProvider implements TableProvider, AutoCloseable {

    private boolean closed = false;
    private Path rootDir;
    private CTable currentTable;
    private Map<String, CTable> listTables;
    private Parser parser = new Parser();
    private HashMap<String, ReentrantReadWriteLock> tableLocks = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    CTableProvider(Path dir) throws IOException {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }
        } catch (IOException e) {
            throw new IOException("Path doesn't exist, can't be created");
        }
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        rootDir = dir;
        listTables = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir)) {
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    try {
                        lock.readLock().lock();
                        tableLocks.put(file.getFileName().toString(), new ReentrantReadWriteLock(true));
                        CTable table = new CTable(file, tableLocks.get(file.getFileName().toString()));
                        listTables.put(file.getFileName().toString(), table);
                    } finally {
                        lock.readLock().unlock();
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't load the database: " + e.getMessage());
        }
    }

    public Set<String> listTables() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        return listTables.keySet();
    }

    @Override
    public String toString() throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        return getClass().getSimpleName() + "[" + rootDir.toAbsolutePath() + "]";
    }

    @Override
    public void close() throws Exception {
        for (Map.Entry<String, CTable> pair : listTables.entrySet()) {
            pair.getValue().close();
        }
        closed = true;
    }

    @Override
    public void removeTable(String name) throws IOException, IllegalArgumentException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        if (name == null) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        lock.writeLock().lock();
        try {
            CTable table = listTables.get(name);
            if (table != null) {
                if (currentTable == table) {
                    currentTable = null;
                }
                listTables.remove(name);
                table.drop();
            } else {
                throw new IllegalStateException("Table doesn't exist");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Table getTable(String name) throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        if (name == null) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        try {
            lock.readLock().lock();
            CTable result = listTables.get(name);
            if (result != null) {
                result.open();
            }
            return result;
        } finally {
             lock.readLock().unlock();
        }
    }

    @Override
    public Table createTable(String name, List<Class<?>> columnTypes) throws IOException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        if (name == null || columnTypes == null || columnTypes.isEmpty()) {
            throw new IllegalArgumentException("Illegal argument: null");
        }
        lock.writeLock().lock();
        try {
            Path path = rootDir.resolve(name);
            if (!listTables.containsKey(name)) {
                if (Files.exists(path)) {
                    if (!Files.isDirectory(path)) {
                        throw new IOException("Destination isn't a directory");
                    }
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                        if (stream.iterator().hasNext()) {
                            throw new IOException("Destination isn't empty");
                        }
                    }
                } else {
                    Files.createDirectory(path);
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                        if (stream.iterator().hasNext()) {
                            throw new IOException("Destination isn't empty");
                        }
                    }
                }
                tableLocks.put(name, new ReentrantReadWriteLock(true));
                listTables.put(name, new CTable(path, columnTypes, tableLocks.get(name)));
                return listTables.get(name);
            }
        } finally {
            lock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        return parser.deserialize(table, value);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        return parser.serialize(table, value);
    }

    @Override
    public Storeable createFor(Table table) throws IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        List<Object> values = new ArrayList<>(table.getColumnsCount());
        return new CStoreable(values);
    }

    @Override
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IllegalStateException {
        if (closed) {
            throw new IllegalStateException("Provider is closed");
        }
        List<Object> listObjects = new ArrayList<>(values);
        if (listObjects.size() != table.getColumnsCount()) {
            throw new ColumnFormatException("Wrong number of columns");
        }
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            if (listObjects.get(i).getClass() != (table.getColumnType(i))) {
                throw new ColumnFormatException("Illegal column type: column " + i
                                                + " should be " + table.getColumnType(1));
            }
        }
        return new CStoreable(listObjects);
    }
}
