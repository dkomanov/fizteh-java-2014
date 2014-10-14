package ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap;

import static ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Utility.handleError;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.DatabaseException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.HandledException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.exception.NoActiveTableException;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Log;
import ru.fizteh.fivt.students.AndrewFedorov.multifilehashmap.support.Utility;

/**
 * Database class responsible for a set of tables assigned to it.
 * 
 * @author phoenix
 * 
 */
public class Database {

    public static Database establishDatabase(Path dbDirectory) {
	return new Database(dbDirectory);
    }

    /**
     * Root directory of all database files
     */
    private final Path dbDirectory;

    /**
     * Table in use.<br/>
     * All operations (like {@code put}, {@code get}, etc.) are performed with
     * this table.
     */
    private Table activeTable;

    /**
     * Establishes a database instance on given folder.<br/>
     * If the folder exists, the old database is used.<br/>
     * If the folder does not exist, a new database is created within the
     * folder.
     * 
     * @param dbDirectory
     * @throws HandledException
     */
    private Database(Path dbDirectory) throws HandledException {
	if (dbDirectory == null) {
	    handleError("Please mention database root directory");
	}

	this.dbDirectory = dbDirectory;

	try {
	    if (!Files.exists(dbDirectory)) {
		Path parent = dbDirectory.getParent();
		if (parent != null && Files.isDirectory(parent)) {
		    Files.createDirectory(dbDirectory);
		} else {
		    handleError("Parent path of the database folder does not exist or is not a directory");
		}
	    } else {
		if (!Files.isDirectory(dbDirectory)) {
		    handleError("Database path is not a directory");
		}

		/*
		 * Checking that this directory contains only directories and
		 * subdirectories contain only directories
		 */
		Path problemFile = Utility
			.checkDirectoryContainsOnlyDirectories(dbDirectory, 2);

		// some file found
		if (problemFile != null) {
		    handleError(
			    new Exception(
				    String.format(
					    "DB directory scan: file '%s' should not exist",
					    problemFile)),
			    "DB directory scan: found inproper files", true);
		}
	    }
	} catch (IOException exc) {
	    handleError(exc, "Cannot scan database directory", true);
	}
    }

    /**
     * Constructor for copying. Only applies given parameters.
     */
    private Database(Path dbDirectory, Table activeTable) {
	this.dbDirectory = dbDirectory;
	this.activeTable = activeTable;
    }

    private void checkCurrentTableIsOpen() throws NoActiveTableException {
	if (activeTable == null) {
	    throw new NoActiveTableException();
	}
    }

    /**
     * Clones the whole database
     */
    @Override
    public Database clone() {
	Database cloneDB = new Database(dbDirectory, activeTable == null ? null
		: activeTable.clone());
	return cloneDB;
    }

    /**
     * Creates a new empty table with specified name
     * 
     * @param tableName
     * @throws HandledException
     *             I/O errors and name duplication errors are here
     */
    public void createTable(String tableName) throws HandledException {
	Path tablePath = dbDirectory.resolve(tableName);
	if (Files.exists(tablePath)) {
	    handleError("tablename exists");
	} else {
	    try {
		Files.createDirectory(tablePath);
	    } catch (IOException exc) {
		handleError(exc, "Failed to create table directory", true);
	    }

	    System.out.println("created");
	}
    }

    /**
     * Deletes given table from file system.
     * 
     * @param tableName
     *            name of table to drop
     * @throws HandledException
     *             if tablename does not exist or failed to delete
     */
    public void dropTable(String tableName) throws HandledException {
	Path tablePath = dbDirectory.resolve(tableName);

	if (!Files.exists(tablePath)) {
	    if (activeTable != null
		    && !activeTable.getTableName().equals(tableName)) {
		activeTable = null;
	    } else {
		handleError("tablename not exists");
	    }
	}

	if (activeTable != null && activeTable.getTableName().equals(tableName)) {
	    activeTable = null;
	}

	Utility.rm(tablePath, "drop");

	// if removed, then print:
	System.out.println("dropped");
    }

    public Table getActiveTable() throws NoActiveTableException {
	checkCurrentTableIsOpen();
	return activeTable;
    }

    public Path getDbDirectory() {
	return dbDirectory;
    }

    /**
     * Writes all changes in the database to file system.
     * 
     * @throws IOException
     */
    public void persistDatabase() throws IOException {
	// actually we have to persist the active table.
	if (activeTable != null) {
	    activeTable.persistTable();
	}
    }

    public void showTables() {
	try (DirectoryStream<Path> dirStream = Files
		.newDirectoryStream(dbDirectory)) {
	    System.out.println("table_name row_count");

	    // Changes in the current table can be not persisted yet.
	    String currentTableName = (activeTable == null ? null : activeTable
		    .getTableName());

	    boolean scanFailed = false;

	    for (Path tableRoot : dirStream) {
		try {
		    Table table = tableRoot.getFileName().toString()
			    .equals(currentTableName) ? activeTable
			    : new Table(tableRoot);

		    System.out.println(String.format("%s %d",
			    tableRoot.getFileName(), table.rowsNumber()));
		} catch (Throwable thr) {
		    System.out.println(String.format("%s corrupt",
			    tableRoot.getFileName()));
		    Log.log(Database.class, thr, "Cannot open table: "
			    + tableRoot.getFileName());
		    scanFailed = true;
		}
	    }

	    if (scanFailed) {
		handleError(null, "show tables finished with errors", false);
	    }
	} catch (IOException exc) {
	    handleError(exc, "Failed to scan database directory", true);
	}
    }

    /**
     * Saves all changes to the current table (if not null) and prepares table
     * with the given name for use.
     * 
     * @param tableName
     *            name of table to use.
     * @throws HandledException
     *             if failed to save changes for current table or failed to load
     *             new table.
     */
    public void useTable(String tableName) throws HandledException {
	Path tablePath = dbDirectory.resolve(tableName);

	if (activeTable != null) {
	    try {
		activeTable.persistTable();
	    } catch (IOException exc) {
		handleError(exc, "Failed to persist table in use: "
			+ activeTable.getTableName(), true);
	    }
	}

	if (!Files.exists(tablePath)) {
	    handleError("tablename not exists");
	}

	Table lastTable = activeTable;

	try {
	    activeTable = new Table(tablePath);
	} catch (DatabaseException exc) {
	    // Will be using old table after this
	    activeTable = lastTable;
	    handleError(exc, "Failed to load new table: " + tableName, true);
	}

	// if everything is ok
	System.out.println("using " + tableName);
    }
}
