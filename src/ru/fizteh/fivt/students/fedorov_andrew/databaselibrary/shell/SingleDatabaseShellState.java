package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.Database;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.TableImpl;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.NoActiveTableException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
    private Shell host;

    @Override
    public void cleanup() {
        // Delete empty files and directories inside tables' directories
        Path dbDirectory = (activeDatabase == null ? null : activeDatabase.getDbDirectory());

        if (dbDirectory != null) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dbDirectory)) {
                for (Path tableDirectory : dirStream) {
                    Utility.removeEmptyFilesAndFolders(tableDirectory);
                }

                Log.log(Shell.class, "Cleaned up successfully");
            } catch (IOException exc) {
                Log.log(Shell.class, exc, "Failed to clean up");
            }
        }
    }

    @Override
    public String getGreetingString() {
        TableImpl table;
        try {
            table = activeDatabase.getActiveTable();
        } catch (NoActiveTableException exc) {
            table = null;
        }
        String greeting = String.format(
                "%s%s $ ",
                (table == null ? "" : (table.getName() + "@")),
                activeDatabase.getDbDirectory());
        return greeting;
    }

    @Override
    public void init(Shell<SingleDatabaseShellState> host)
            throws IllegalArgumentException, DatabaseException {
        if (this.host != null) {
            throw new IllegalStateException("Initialization happened already");
        }
        this.host = host;

        //establishing database
        String dbDirPath = System.getProperty(DB_DIRECTORY_PROPERTY_NAME);
        if (dbDirPath == null) {
            throw new IllegalArgumentException("Please mention database directory");
        }

        activeDatabase = Database.establishDatabase(Paths.get(dbDirPath));
    }

    @Override
    public void persist() throws DatabaseException {
        activeDatabase.commit();
    }

    @Override
    public void exit(int exitCode) {
        cleanup();
        Log.close();
        System.exit(exitCode);
    }

    /**
     * Returns database user works with.
     * @return
     */
    public Database getActiveDatabase() {
        return activeDatabase;
    }

    @Override
    public Map<String, Command<SingleDatabaseShellState>> getCommands() {
        return COMMANDS_CONTAINER.getCommands();
    }
}
