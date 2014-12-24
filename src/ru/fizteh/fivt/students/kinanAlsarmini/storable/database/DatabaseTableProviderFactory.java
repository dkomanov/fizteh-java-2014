package ru.fizteh.fivt.students.kinanAlsarmini.storable.database;

import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String directory) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }

        if (directory.trim().isEmpty()) {
            throw new IllegalArgumentException("directory's name cannot be empty");
        }

        File databaseDirectory = new File(directory);

        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("cannot create database in file. Provide a directory, please");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException("provider is unavailable");
            }
        }

        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
