package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

public class DBTableProviderFactory implements TableProviderFactory {
    // left until better times
    // private static List<Path> fileLocks = Collections
    // .synchronizedList(new LinkedList<Path>());
    //
    // static class LockDeleter implements Runnable {
    // @Override
    // public void run() {
    // for (Path path : fileLocks) {
    // DBTableProvider.cancelLock(path);
    // }
    // }
    // }
    //
    // private static LockDeleter lockDeleter = null;

    @Override
    public DBTableProvider create(String dir) throws DatabaseException,
	    IllegalArgumentException {
	if (dir == null) {
	    throw new IllegalArgumentException("Directory must not be null");
	}

	Path databaseRoot = Paths.get(dir).normalize();
	if (!Files.exists(databaseRoot)) {
	    if (databaseRoot.getParent() == null
		    || !Files.isDirectory(databaseRoot.getParent())) {
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
		Path problemFile = Utility
			.checkDirectoryContainsOnlyDirectories(databaseRoot, 2);

		if (problemFile != null) {
		    throw new IllegalArgumentException(
			    "DB directory scan: found inproper files",
			    new DatabaseException("File should not exist: "
				    + problemFile));
		}
	    } catch (IOException exc) {
		throw new IllegalArgumentException(
			"Failed to scan database directory " + dir, exc);
	    }
	}

	try {
	    return new DBTableProvider(databaseRoot);
	} catch (DatabaseException exc) {
	    throw new IllegalArgumentException(exc.getMessage(), exc);
	}
    }
}
