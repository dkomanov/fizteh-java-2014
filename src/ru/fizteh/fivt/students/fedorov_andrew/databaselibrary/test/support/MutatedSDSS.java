package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.Database;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Shell;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.SingleDatabaseShellState;

import java.nio.file.Paths;

/**
 * Mutated SingleDatabaseShellState that limits calls to {@link ru.fizteh.fivt.students.fedorov_andrew
 * .databaselibrary.shell.ShellState#persist()}
 * method.
 */
public class MutatedSDSS extends SingleDatabaseShellState {
    private int commitCallsLeft;

    private MutatedDatabase mutatedActiveDatabase;

    public MutatedSDSS(int commitCallsLeft) {
        if (commitCallsLeft < 0) {
            throw new IllegalArgumentException("commitCallsLeft must be positive or 0");
        }
        this.commitCallsLeft = commitCallsLeft;
    }

    @Override
    public void init(Shell<SingleDatabaseShellState> host)
            throws IllegalArgumentException, DatabaseIOException {
        this.mutatedActiveDatabase = new MutatedDatabase(
                Paths.get(System.getProperty(DB_DIRECTORY_PROPERTY_NAME)), commitCallsLeft);
    }

    @Override
    public Database getActiveDatabase() {
        return mutatedActiveDatabase;
    }
}
