package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Database directory is null");
        }

        Path dirPath = Paths.get(path);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot create database " + dirPath.toString(), e);
            }
        }
        return new Database(path);
    }
}
