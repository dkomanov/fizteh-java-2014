package ru.fizteh.fivt.students.LebedevAleksey.junit;

import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.DatabaseFileStructureException;
import ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.LoadOrSaveException;

public class Database extends ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap.Database {
    public Database(String path) throws DatabaseFileStructureException, LoadOrSaveException {
        super(path);
    }

    @Override
    protected Table generateTable(String name) throws DatabaseFileStructureException {
        return new Table(name, getRootDirectoryPath());
    }
}
