package ru.fizteh.fivt.students.pavel_voropaev.project.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProviderFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseFactory implements TableProviderFactory {

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Database directory is null");
        }

        Path dirPath = Paths.get(dir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create database "
                        + dirPath.toString() + e.getMessage(), e);
            }
        }
        return new Database(dir);
    }
}
