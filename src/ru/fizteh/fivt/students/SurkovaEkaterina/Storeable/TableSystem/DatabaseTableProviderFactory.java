package ru.fizteh.fivt.students.SurkovaEkaterina.Storeable.TableSystem;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import java.io.File;
import java.io.IOException;

public class DatabaseTableProviderFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String directory) throws IOException {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null!");
        }

        if (directory.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory's name cannot be empty!");
        }

        File databaseDirectory = new File(directory);
        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("Cannot create database in file. Provide a directory, please");
        }

        if (!databaseDirectory.exists()) {
            if (!databaseDirectory.mkdir()) {
                throw new IOException("Provider is unavailable!");
            }
        }
        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
