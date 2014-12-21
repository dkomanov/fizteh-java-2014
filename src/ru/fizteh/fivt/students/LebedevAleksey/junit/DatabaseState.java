package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;

public class DatabaseState extends ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseState {
    public DatabaseState() throws DatabaseFileStructureException, LoadOrSaveException {
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            throw new DatabaseFileStructureException("Database directory doesn't set");
        } else {
            database = new Database(directoryPath);
        }
    }
}
