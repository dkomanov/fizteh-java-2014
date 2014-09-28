package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.nio.file.Path;
import java.util.HashMap;

public class DbConnector implements AutoCloseable {
    Database db;

    DbConnector(Path dbPath) {
        db = new Database(dbPath);
    }

    @Override
    public void close() throws Exception {
        db.unload();
    }
}
