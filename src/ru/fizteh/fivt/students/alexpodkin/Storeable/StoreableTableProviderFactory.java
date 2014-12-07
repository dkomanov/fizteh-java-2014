package ru.fizteh.fivt.students.alexpodkin.Storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Alex on 23.11.14.
 */
public class StoreableTableProviderFactory implements TableProviderFactory {

    public StoreableTableProviderFactory() {
    }

    @Override
    public TableProvider create(String path) throws IOException, IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("path shouldn't be null");
        }
        File db = new File(path);
        if (db.exists() && !db.isDirectory()) {
            throw new IllegalArgumentException("It's not a directory");
        }
        if (!db.exists()) {
            if (!db.mkdir()) {
                throw new IOException("Can't create a directory");
            }
        }
        return new StoreableTableProvider(path);
    }
}
