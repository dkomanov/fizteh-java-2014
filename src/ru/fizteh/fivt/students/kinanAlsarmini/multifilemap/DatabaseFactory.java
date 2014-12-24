package ru.fizteh.fivt.students.kinanAlsarmini.multifilemap;

import ru.fizteh.fivt.storage.strings.*;

import java.io.File;

public class DatabaseFactory implements TableProviderFactory {
    public TableProvider create(String directory) {

        if (directory == null || directory.isEmpty()) {
            throw new IllegalArgumentException("directory name cannot be null");
        }

        File databaseDirectory = new File(directory);

        if (databaseDirectory.isFile()) {
            throw new IllegalArgumentException("set database directory, not file");
        }

        if (!databaseDirectory.exists()) {
            databaseDirectory.mkdir();
        }

        return new DatabaseTableProvider(databaseDirectory.getAbsolutePath());
    }
}
