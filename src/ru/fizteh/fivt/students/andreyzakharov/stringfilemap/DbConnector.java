package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DbConnector implements AutoCloseable, TableProvider {
    Path dbRoot;
    Map<String, FileMap> tables;
    FileMap activeTable;

    DbConnector(Path dbPath) throws ConnectionInterruptException {
        if (!Files.exists(dbPath)) {
            try {
                Files.createFile(dbPath);
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
        return null;
    }

    @Override
    public Table createTable(String name) {
        return null;
    }

    @Override
    public void removeTable(String name) {
        return;
    }

    public void open() throws ConnectionInterruptException {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        FileMap table = new FileMap(file);
                        try {
                            table.load();
                        } catch (ConnectionInterruptException e) {
                            continue;
                        }
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
                for (FileMap table : tables.values()) {
                    table.unload();
                }
            } catch (ConnectionInterruptException e) {
                // suppress the exception
            }
        }
    }
}
