package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.db.Database;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.exception.DatabaseIOException;

import java.nio.file.Path;

public class MutatedDatabase extends Database {
    private int commitCallsLeft;

    public MutatedDatabase(Path dbDirectory, int commitCallsLeft) throws DatabaseIOException {
        super(dbDirectory);
        this.commitCallsLeft = commitCallsLeft;
    }

    @Override
    public int commit() throws DatabaseIOException {
        if (commitCallsLeft == 0) {
            throw new DatabaseIOException("Fail on commit [test mode]");
        }

        commitCallsLeft--;
        return super.commit();
    }
}
