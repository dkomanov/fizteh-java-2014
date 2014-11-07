package ru.fizteh.fivt.students.standy66_new.storage.structured;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by andrew on 07.11.14.
 */
public class StructuredDatabaseFactory implements TableProviderFactory {
    @Override
    public TableProvider create(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("dir is null or empty");
        }
        File databaseFile = new File(path).getAbsoluteFile();
        return new StructuredDatabase(databaseFile);
    }
}
