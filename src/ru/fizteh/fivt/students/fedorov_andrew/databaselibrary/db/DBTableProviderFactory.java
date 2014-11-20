package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DBTableProviderFactory implements TableProviderFactory {

    private void checkDatabaseDirectory(final Path databaseRoot) throws DatabaseIOException {
        if (!Files.isDirectory(databaseRoot)) {
            throw new DatabaseIOException("Database root must be a directory");
        }

        try (DirectoryStream<Path> tableDirs = Files.newDirectoryStream(databaseRoot)) {
            for (Path tableDirectory : tableDirs) {
                if (!Files.isDirectory(tableDirectory)) {
                    throw new DatabaseIOException(
                            "Non-directory path found in database folder: " + tableDirectory.getFileName());
                }
            }
        } catch (IOException exc) {
            throw new DatabaseIOException(
                    "Failed to scan database directory: " + exc.getMessage(), exc);
        }
    }

    @Override
    public DBTableProvider create(String dir) throws IllegalArgumentException, DatabaseIOException {
        Utility.checkNotNull(dir, "Directory");

        Path databaseRoot = Paths.get(dir).normalize();
        if (!Files.exists(databaseRoot)) {
            if (databaseRoot.getParent() == null || !Files.isDirectory(databaseRoot.getParent())) {
                throw new DatabaseIOException(
                        "Database directory parent path does not exist or is not a directory");
            }

            try {
                Files.createDirectory(databaseRoot);
            } catch (IOException exc) {
                throw new DatabaseIOException("Failed to establish database on path " + dir, exc);
            }
        } else {
            checkDatabaseDirectory(databaseRoot);
        }

        return new DBTableProvider(databaseRoot);
    }
}
