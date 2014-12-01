package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MultiFileTableProvider implements AutoCloseable, TableProvider {
    Path dbRoot;
    Map<String, MultiFileTable> tables;
    MultiFileTable activeTable;

    MultiFileTableProvider(Path dbPath) throws ConnectionInterruptException {
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
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        return tables.get(name);
    }

    public MultiFileTable getCurrent() {
        return activeTable;
    }

    public Map<String, MultiFileTable> getAllTables() {
        return tables;
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        if (!tables.containsKey(name)) {
            try {
                tables.put(name, new MultiFileTable(dbRoot.resolve(name)));
            } catch (ConnectionInterruptException e) {
                return null;
            }
            return tables.get(name);
        } else {
            return null;
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
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
            throw new IllegalStateException("table does not exist");
        }
    }

    public void useTable(String name) throws IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException("null argument");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }
        MultiFileTable table = tables.get(name);
        if (table != null) {
            activeTable = table;
        } else {
            throw new IllegalStateException("table does not exist");
        }
    }

    public void open() throws ConnectionInterruptException {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        MultiFileTable table = new MultiFileTable(file);
                        table.load();
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                throw new ConnectionInterruptException("connection: unable to load the database");
            }
        }
    }

    @Override
    public void close() {
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
