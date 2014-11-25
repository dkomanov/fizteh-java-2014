package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class DatabaseFactory implements TableProviderFactory{
    Database database;

    @Override
    public TableProvider create(String dir) {
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            throw new IllegalArgumentException("Database directory doesn't set.");
        } else {
            database = new Database(directoryPath);
        }
        return database;
    }
}
