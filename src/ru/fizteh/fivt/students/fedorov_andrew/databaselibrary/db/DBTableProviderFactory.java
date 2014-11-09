package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DBFileCorruptException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DBTableProviderFactory implements TableProviderFactory {

    /**
     * @param databaseRoot
     * @return
     */
    private void checkDatabaseDirectory(final Path databaseRoot) throws DatabaseException {
        if (!Files.isDirectory(databaseRoot)) {
            throw new DBFileCorruptException("Database root must be a directory");
        }

        try (DirectoryStream<Path> tableDirs = Files.newDirectoryStream(databaseRoot)) {
            for (Path tableDirectory : tableDirs) {
                if (!Files.isDirectory(tableDirectory)) {
                    throw new DBFileCorruptException(
                            "Non-directory path found in database folder: " + tableDirectory.getFileName());
                }
            }
        } catch (IOException exc) {
            throw new DatabaseException(
                    "Failed to scan database directory: " + exc.getMessage(), exc);
        }
    }

    @Override
    public DBTableProvider create(String dir) throws IllegalArgumentException {
        if (dir == null) {
            throw new IllegalArgumentException("Directory must not be null");
        }

        Path databaseRoot = Paths.get(dir).normalize();
        if (!Files.exists(databaseRoot)) {
            if (databaseRoot.getParent() == null || !Files.isDirectory(databaseRoot.getParent())) {
                throw new IllegalArgumentException(
                        "Database directory parent path does not exist or is not a directory");
            }

            try {
                Files.createDirectory(databaseRoot);
            } catch (IOException exc) {
                throw new IllegalArgumentException(
                        "Failed to establish database on path " + dir, exc);
            }
        } else {
            try {
                checkDatabaseDirectory(databaseRoot);
            } catch (DatabaseException exc) {
                throw new IllegalArgumentException(exc.getMessage(), exc);
            }
        }

        try {
            return new DBTableProvider(databaseRoot);
        } catch (DatabaseException exc) {
            throw new IllegalArgumentException(exc.getMessage(), exc);
        }
    }
}
