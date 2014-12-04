package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.Database;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.ExitRequest;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents actual task implementation: work from terminal with a database, whose
 * location in file system is given.
 */
public class SingleDatabaseShellState implements ShellState<SingleDatabaseShellState> {
    /**
     * Name of environment property; value stored there is database location.
     */
    public static final String DB_DIRECTORY_PROPERTY_NAME = "fizteh.db.dir";

    /**
     * Our proxy command container.
     */
    private static final Commands COMMANDS_CONTAINER = Commands.obtainInstance();

    /**
     * Database that user works with via terminal.
     */
    private Database activeDatabase;

    /**
     * Shell representing terminal.
     */
    private Shell<SingleDatabaseShellState> host;

    @Override
    public void cleanup() {
        // Delete empty files and directories inside tables' directories
        Path dbDirectory = (getActiveDatabase() == null ? null : getActiveDatabase().getDbDirectory());

        if (dbDirectory != null) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dbDirectory)) {
                for (Path tableDirectory : dirStream) {
                    Utility.removeEmptyFilesAndFolders(tableDirectory);
                }

                Log.log(SingleDatabaseShellState.class, "Cleaned up successfully");
            } catch (IOException exc) {
                Log.log(SingleDatabaseShellState.class, exc, "Failed to clean up");
            }
        }
    }

    @Override
    public String getGreetingString() {
        Table table;
        try {
            table = getActiveDatabase().getActiveTable();
        } catch (NoActiveTableException exc) {
            table = null;
        }
        return String.format(
                "%s%s $ ",
                (table == null ? "" : (table.getName() + '@')),
                getActiveDatabase().getDbDirectory());
    }

    @Override
    public void init(Shell<SingleDatabaseShellState> host)
            throws IllegalArgumentException, DatabaseIOException {
        Objects.requireNonNull(host, "Host shell must not be null");

        if (this.host != null) {
            throw new IllegalStateException("Initialization happened already");
        }
        this.host = host;

        //establishing database
        String dbDirPath = System.getProperty(DB_DIRECTORY_PROPERTY_NAME);
        if (dbDirPath == null) {
            throw new IllegalArgumentException("Please mention database directory");
        }

        activeDatabase = new Database(Paths.get(dbDirPath));
    }

    @Override
    public void persist() throws IOException {
        getActiveDatabase().commit();
    }

    @Override
    public void prepareToExit(int exitCode) throws ExitRequest {
        Log.log(SingleDatabaseShellState.class, "Preparing to exit with code " + exitCode);
        cleanup();
        try {
            activeDatabase.close();
        } catch (Exception exc) {
            Log.log(SingleDatabaseShellState.class, exc, "Failed to close database properly");
        }
        Log.close();
        throw new ExitRequest(exitCode);
    }

    /**
     * Returns database user works with.
     */
    public Database getActiveDatabase() {
        return activeDatabase;
    }

    public TableProvider getProvider() {
        return getActiveDatabase().getProvider();
    }

    public Table getActiveTable() throws NoActiveTableException {
        return getActiveDatabase().getActiveTable();
    }

    @Override
    public Map<String, Command<SingleDatabaseShellState>> getCommands() {
        return COMMANDS_CONTAINER.getCommands();
    }
}
