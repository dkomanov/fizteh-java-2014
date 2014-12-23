package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.TableSystem;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import java.io.File;
import java.io.IOException;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String directory) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Directory cannot be null!");
        }

        if (directory.trim().isEmpty()) {
            throw new IllegalArgumentException(this.getClass().toString() + ": Directory's name cannot be empty!");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException(this.getClass().toString()
                    + ": Cannot create database in file. Provide a directory, please");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException(this.getClass().toString() + ": Provider is unavailable!");
            }
        }
        return new ThreadSafeDatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
